// JwtUtils.java - FILE HOÀN CHỈNH (SỬA LỖI JWT KEY)
package com.pascs.security.jwt;

import com.pascs.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${pascs.app.jwtSecret}")
    private String jwtSecret;

    @Value("${pascs.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Tạo secret key an toàn
    private SecretKey getSigningKey() {
        // Nếu jwtSecret đủ dài, sử dụng nó
        if (jwtSecret != null && jwtSecret.length() >= 64) {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } else {
            // Tạo key tự động an toàn
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    // ✅ Tạo token JWT dựa trên thông tin người dùng
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ✅ Lấy username từ token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Kiểm tra tính hợp lệ của token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}