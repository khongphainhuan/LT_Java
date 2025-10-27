package com.pascs.controller;

import com.pascs.model.User;
import com.pascs.payload.request.UpdateProfileRequest;
import com.pascs.payload.request.ChangePasswordRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.payload.response.UserProfileResponse;
import com.pascs.repository.UserRepository;
import com.pascs.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    // Lấy thông tin profile của user hiện tại
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> user = userRepository.findById(userDetails.getId());
        
        if (user.isPresent()) {
            UserProfileResponse profileResponse = new UserProfileResponse(
                user.get().getId(),
                user.get().getUsername(),
                user.get().getEmail(),
                user.get().getFullName(),
                user.get().getPhoneNumber(),
                user.get().getAddress(),
                user.get().getRole(),
                user.get().isPriorityEligible(),
                user.get().getCreatedAt()
            );
            return ResponseEntity.ok(profileResponse);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }
    }

    // Cập nhật thông tin profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOptional = userRepository.findById(userDetails.getId());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Cập nhật thông tin
            if (updateRequest.getFullName() != null) {
                user.setFullName(updateRequest.getFullName());
            }
            if (updateRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(updateRequest.getPhoneNumber());
            }
            if (updateRequest.getAddress() != null) {
                user.setAddress(updateRequest.getAddress());
            }
            if (updateRequest.getPriorityEligible() != null) {
                user.setPriorityEligible(updateRequest.getPriorityEligible());
            }
            
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("Profile updated successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }
    }

    // Đổi mật khẩu
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOptional = userRepository.findById(userDetails.getId());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Kiểm tra mật khẩu cũ
            if (!encoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Old password is incorrect!"));
            }
            
            // Cập nhật mật khẩu mới
            user.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }
    }

    // ADMIN: Lấy danh sách tất cả users
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // ADMIN: Cập nhật role user
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestParam User.UserRole role) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("User role updated successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }
    }
}