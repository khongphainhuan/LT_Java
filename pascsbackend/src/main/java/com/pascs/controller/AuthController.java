package com.pascs.controller;

import com.pascs.model.User;
import com.pascs.payload.request.LoginRequest;
import com.pascs.payload.request.SignupRequest;
import com.pascs.payload.response.JwtResponse;
import com.pascs.payload.response.MessageResponse;
import com.pascs.repository.UserRepository;
import com.pascs.security.jwt.JwtUtils;
import com.pascs.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    // Đăng nhập : /api/auth/signin
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Attempting login for: " + loginRequest.getUsername());

            // DEBUG: Kiểm tra user có tồn tại không
            Optional<User> userByUsername = userRepository.findByUsername(loginRequest.getUsername());
            Optional<User> userByEmail = userRepository.findByEmail(loginRequest.getUsername());

            System.out.println("📊 User found by username: " + userByUsername.isPresent());
            System.out.println("📊 User found by email: " + userByEmail.isPresent());

            userByUsername.ifPresent(u -> {
                System.out.println("🔑 Stored password hash: " + u.getPassword());
                System.out.println("👤 User role: " + u.getRole());
                System.out.println("✅ User enabled: " + u.isEnabled());
            });

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            System.out.println("✅ Authentication successful for: " + userDetails.getUsername());
            System.out.println("🎯 User ID: " + userDetails.getId());
            System.out.println("📧 User Email: " + userDetails.getEmail());
            System.out.println("🔐 JWT Token generated: " + jwt);

            // DEBUG: In ra thông tin authorities
            System.out.println("🔐 Authorities from authentication:");
            userDetails.getAuthorities().forEach(auth ->
                    System.out.println("   - Authority: " + auth.getAuthority()));

            // Chọn role trả về (nếu có nhiều authority, ưu tiên ADMIN > STAFF > CITIZEN)
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            User.UserRole finalRole = User.UserRole.CITIZEN; // default
            for (GrantedAuthority a : authorities) {
                String auth = (a == null || a.getAuthority() == null) ? "" : a.getAuthority().trim().toUpperCase();
                if (auth.endsWith("ADMIN")) {
                    finalRole = User.UserRole.ADMIN;
                    break;
                } else if (auth.endsWith("STAFF")) {
                    finalRole = User.UserRole.STAFF;
                } else if (auth.endsWith("CITIZEN")) {
                    finalRole = User.UserRole.CITIZEN;
                }
            }

            System.out.println("🎭 Final role for response: " + finalRole);

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    finalRole));
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            System.out.println("🔍 Error type: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password"));
        }
    }

    // Đăng ký : /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            System.out.println("📝 Attempting registration for: " + signUpRequest.getUsername());

            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                System.out.println("❌ Username already taken: " + signUpRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                System.out.println("❌ Email already in use: " + signUpRequest.getEmail());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }

            // Create new user's account
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setFullName(signUpRequest.getFullName());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setAddress(signUpRequest.getAddress());
            user.setRole(User.UserRole.CITIZEN); // Default role for new registrations
            user.setEnabled(true);

            User savedUser = userRepository.save(user);

            System.out.println("✅ User registered successfully: " + savedUser.getUsername());
            System.out.println("🔑 Password encoded: " + savedUser.getPassword());
            System.out.println("🎭 Role assigned: " + savedUser.getRole());

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Registration failed!"));
        }
    }

    // Kiểm tra token
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !authentication.getPrincipal().equals("anonymousUser")) {

            System.out.println("🔐 User authenticated: " + authentication.getName());
            authentication.getAuthorities().forEach(auth ->
                    System.out.println("🔐 Current authority: " + auth.getAuthority()));

            return ResponseEntity.ok(new MessageResponse("Authenticated"));
        }
        System.out.println("❌ User not authenticated");
        return ResponseEntity.status(401).body(new MessageResponse("Not authenticated"));
    }

    // Lấy thông tin user hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(new MessageResponse("Not authenticated"));
        }

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok(new JwtResponse(
                null, // No token needed for session auth
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
            ));
        }
        
        return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
    }

    // Kiểm tra health
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        System.out.println("🏥 Health check called");
        return ResponseEntity.ok(new MessageResponse("Auth service is running"));
    }
}
