package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    @GetMapping({"/", "/index"})
    public String home() {
        return "index";  // Trang giới thiệu
    }
}