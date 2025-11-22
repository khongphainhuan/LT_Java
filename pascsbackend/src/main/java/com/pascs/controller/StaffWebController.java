package com.pascs.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffWebController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Thêm thông tin user vào model nếu cần
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("username", authentication.getName());
            }
            return "staff/dashboard";
        } catch (Exception e) {
            System.err.println("❌ Error rendering staff dashboard: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/queue")
    public String queue() {
        return "staff/queue";
    }

    @GetMapping("/applications")
    public String applications() {
        return "staff/applications";
    }

    @GetMapping("/application-detail")
    public String applicationDetail() {
        return "staff/application-detail";
    }

    @GetMapping("/profile")
    public String profile() {
        return "staff/profile";
    }
}