package com.pascs.service;

import com.pascs.model.User;
import com.pascs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println("🔍 Loading user by username/email: " + login);
        
        User user = userRepository.findByUsername(login)
                .orElseGet(() -> {
                    System.out.println("⚠️ User not found by username, trying email...");
                    return userRepository.findByEmail(login)
                            .orElseThrow(() -> {
                                System.out.println("❌ User not found with username or email: " + login);
                                return new UsernameNotFoundException(
                                        "User Not Found with username or email: " + login
                                );
                            });
                });

        System.out.println("✅ User found: " + user.getUsername());
        System.out.println("   - Email: " + user.getEmail());
        System.out.println("   - Role: " + (user.getRole() != null ? user.getRole().name() : "NULL"));
        System.out.println("   - Enabled: " + user.isEnabled());
        System.out.println("   - Password hash length: " + (user.getPassword() != null ? user.getPassword().length() : 0));

        return UserDetailsImpl.build(user);
    }
}
