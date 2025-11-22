package com.pascs.security.jwt;

import com.pascs.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Chỉ xử lý JWT cho API requests, bỏ qua form login và static resources
        String requestPath = request.getRequestURI();
        
        // Bỏ qua filter cho form login và static resources
        if (requestPath.startsWith("/auth/") || 
            requestPath.startsWith("/css/") || 
            requestPath.startsWith("/js/") || 
            requestPath.startsWith("/images/") ||
            requestPath.startsWith("/lib/") ||
            requestPath.equals("/favicon.ico") ||
            requestPath.equals("/") ||
            requestPath.equals("/index")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Chỉ xử lý JWT cho API requests
        if (!requestPath.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Kiểm tra nếu user đã được authenticated rồi (từ form login)
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("JWT authentication set for user: {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication from JWT: {}", e.getMessage());
            // Không throw exception, để Spring Security xử lý authentication khác
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}