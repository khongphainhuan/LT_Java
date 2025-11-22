package com.pascs.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String redirectToRoleDashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            System.out.println("User: " + authentication.getName());
            System.out.println("Roles: " + authorities);
            
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                return "redirect:/staff/dashboard";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"))) {
                return "redirect:/citizen/dashboard";
            }
        }
        return "redirect:/auth/login";
    }
}