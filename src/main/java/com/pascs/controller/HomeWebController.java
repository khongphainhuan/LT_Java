package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    @GetMapping("/")
    public String home() {
        // Trang chủ chuyển hướng đến trang đăng nhập
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "common/access-denied";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "common/error";
    }

    @GetMapping("/404")
    public String notFound() {
        return "common/404";
    }
}
