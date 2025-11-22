package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestWebController {

    @GetMapping("/test-login")
    public String testLogin() {
        return "auth/login";
    }
    
    @GetMapping("/test-admin")
    public String testAdmin() {
        return "admin/dashboard";
    }
    
    @GetMapping("/test-citizen")
    public String testCitizen() {
        return "citizen/dashboard";
    }
    
    @GetMapping("/test-staff")
    public String testStaff() {
        return "staff/dashboard";
    }
    
    @GetMapping("/test-index")
    public String testIndex() {
        return "index";
    }
    
    @GetMapping("/test-services")
    public String testServices() {
        return "admin/services";
    }
    
    @GetMapping("/test-queue")
    public String testQueue() {
        return "staff/queue";
    }
    
    @GetMapping("/test-applications")
    public String testApplications() {
        return "citizen/my-applications";
    }
}