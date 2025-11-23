package com.pascs.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Thêm thông tin user vào model nếu cần
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("username", authentication.getName());
            }
            // Thêm thông tin cho fragments
            model.addAttribute("basePath", "/admin");
            model.addAttribute("homeUrl", "/admin/dashboard");
            model.addAttribute("profileUrl", "/profile");
            return "admin/dashboard";
        } catch (Exception e) {
            System.err.println("❌ Error rendering admin dashboard: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/services")
    public String services() {
        return "admin/services";
    }

    @GetMapping("/service-form")
    public String serviceForm() {
        return "admin/service-form";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "admin/feedback";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }

    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}
