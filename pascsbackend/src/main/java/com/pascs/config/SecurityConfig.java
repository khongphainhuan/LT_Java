package com.pascs.config;

import com.pascs.security.jwt.AuthEntryPointJwt;
import com.pascs.security.jwt.AuthTokenFilter;
import com.pascs.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // Hiển thị lỗi rõ ràng hơn
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                
                if (authentication == null) {
                    System.out.println("❌ Authentication is null, redirecting to /auth/login");
                    response.sendRedirect("/auth/login?error=true");
                    return;
                }
                
                System.out.println("✅ Login successful for user: " + authentication.getName());
                System.out.println("🔐 Authorities: " + authentication.getAuthorities());
                
                String targetUrl = determineTargetUrl(authentication);
                System.out.println("🎯 Redirecting to: " + targetUrl);
                
                clearAuthenticationAttributes(request);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            }

            protected String determineTargetUrl(Authentication authentication) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                
                System.out.println("🔍 All authorities: " + authorities);
                
                if (authorities == null || authorities.isEmpty()) {
                    System.out.println("⚠️ No authorities found, redirecting to /auth/login");
                    return "/auth/login";
                }
                
                for (GrantedAuthority authority : authorities) {
                    String role = authority.getAuthority();
                    if (role == null || role.trim().isEmpty()) {
                        continue;
                    }
                    
                    System.out.println("🔍 Checking authority: " + role);
                    
                    // Normalize role to uppercase for comparison
                    String roleUpper = role.trim().toUpperCase();
                    
                    // Check for ADMIN role (highest priority) - exact match first
                    if (roleUpper.equals("ROLE_ADMIN")) {
                        System.out.println("✅ Matched ADMIN role, redirecting to /admin/dashboard");
                        return "/admin/dashboard";
                    }
                    // Check for STAFF role - exact match first
                    if (roleUpper.equals("ROLE_STAFF")) {
                        System.out.println("✅ Matched STAFF role, redirecting to /staff/dashboard");
                        return "/staff/dashboard";
                    }
                    // Check for CITIZEN role - exact match first
                    if (roleUpper.equals("ROLE_CITIZEN")) {
                        System.out.println("✅ Matched CITIZEN role, redirecting to /citizen/dashboard");
                        return "/citizen/dashboard";
                    }
                }
                
                // Fallback: check with contains for any edge cases
                for (GrantedAuthority authority : authorities) {
                    String role = authority.getAuthority();
                    if (role == null) continue;
                    
                    String roleUpper = role.trim().toUpperCase();
                    
                    // Fallback checks
                    if (roleUpper.contains("ADMIN") && !roleUpper.contains("CITIZEN") && !roleUpper.contains("STAFF")) {
                        System.out.println("✅ Matched ADMIN role (fallback), redirecting to /admin/dashboard");
                        return "/admin/dashboard";
                    }
                    if (roleUpper.contains("STAFF") && !roleUpper.contains("ADMIN")) {
                        System.out.println("✅ Matched STAFF role (fallback), redirecting to /staff/dashboard");
                        return "/staff/dashboard";
                    }
                    if (roleUpper.contains("CITIZEN") && !roleUpper.contains("ADMIN") && !roleUpper.contains("STAFF")) {
                        System.out.println("✅ Matched CITIZEN role (fallback), redirecting to /citizen/dashboard");
                        return "/citizen/dashboard";
                    }
                }
                
                System.out.println("⚠️ No matching role found, redirecting to /auth/login");
                return "/auth/login";
            }
        };
        
        // Configure redirect strategy to use context-relative URLs
        handler.setUseReferer(false);
        handler.setAlwaysUseDefaultTargetUrl(false);
        
        return handler;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            System.out.println("❌ Access denied for: " + request.getRequestURI());
            System.out.println("👤 User: " + (request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous"));
            System.out.println("🔍 Access denied exception: " + accessDeniedException.getMessage());
            response.sendRedirect("/auth/login?accessDenied=true");
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                org.springframework.security.core.AuthenticationException exception)
                    throws IOException, jakarta.servlet.ServletException {
                
                String username = request.getParameter("username");
                System.out.println("❌ Login failed for username: " + username);
                System.out.println("🔍 Exception type: " + exception.getClass().getName());
                System.out.println("🔍 Exception message: " + exception.getMessage());
                
                if (exception.getCause() != null) {
                    System.out.println("🔍 Exception cause: " + exception.getCause().getMessage());
                }
                
                super.setDefaultFailureUrl("/auth/login?error=true");
                super.onAuthenticationFailure(request, response, exception);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Disable CSRF cho API
        )
        
        .authorizeHttpRequests(auth -> auth
            // Public routes
            .requestMatchers("/", "/index", "/auth/**",
                            "/css/**", "/js/**", "/images/**", "/lib/**",
                            "/favicon.ico", "/swagger-ui/**", "/api-docs/**",
                            "/test-/**").permitAll()

            // API routes
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/**").authenticated()

            // Role-based routes
            .requestMatchers("/citizen/**").hasAnyRole("CITIZEN", "ADMIN")
            .requestMatchers("/staff/**").hasAnyRole("STAFF", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")

            .anyRequest().authenticated()
        )

        .formLogin(form -> form
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(authenticationSuccessHandler())
            .failureHandler(authenticationFailureHandler())
            .permitAll()
        )

        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/auth/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        )

        .exceptionHandling(ex -> ex
            // Xử lý khi chưa đăng nhập (web form)
            .authenticationEntryPoint((request, response, authException) -> {
                String requestPath = request.getRequestURI();
                System.out.println("🔒 Authentication required for: " + requestPath);
                
                // Nếu là API request, dùng JSON response
                if (requestPath.startsWith("/api/")) {
                    unauthorizedHandler.commence(request, response, authException);
                } else {
                    // Nếu là web request, redirect đến login
                    response.sendRedirect("/auth/login?error=true");
                }
            })
            // Xử lý khi không có quyền truy cập
            .accessDeniedHandler(accessDeniedHandler())
        );

        // Authentication provider
        http.authenticationProvider(authenticationProvider());
        
        // JWT Filter chỉ áp dụng cho API requests, bỏ qua form login
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
