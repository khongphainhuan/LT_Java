package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffWebController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "staff/dashboard";
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
