package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
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
