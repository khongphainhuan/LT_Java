package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/services")
public class ServiceWebController {

    @GetMapping("")
    public String listServices() {
        return "admin/services";
    }

    @GetMapping("/form")
    public String serviceForm() {
        return "admin/service-form";
    }
}
