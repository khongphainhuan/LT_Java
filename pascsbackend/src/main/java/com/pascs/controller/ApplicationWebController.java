package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/applications")
public class ApplicationWebController {

    @GetMapping("")
    public String viewApplications() {
        return "staff/applications";
    }

    @GetMapping("/detail")
    public String viewApplicationDetail() {
        return "staff/application-detail";
    }
}
