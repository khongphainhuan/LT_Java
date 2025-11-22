package com.pascs.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/citizen")
public class CitizenWebController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Thêm thông tin user vào model nếu cần
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("username", authentication.getName());
            }
            return "citizen/dashboard";
        } catch (Exception e) {
            System.err.println("❌ Error rendering citizen dashboard: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/my-applications")
    public String myApplications() {
        return "citizen/my-applications";
    }

    @GetMapping("/take-number")
    public String takeNumber() {
        return "citizen/take-number";
    }

    @GetMapping("/appointments")
    public String appointments() {
        return "citizen/appointments";
    }

    @GetMapping("/profile")
    public String profile() {
        return "citizen/profile";
    }
}