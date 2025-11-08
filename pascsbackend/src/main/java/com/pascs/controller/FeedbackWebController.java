package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/feedback")
public class FeedbackWebController {

    @GetMapping("")
    public String feedbackPage() {
        return "admin/feedback";
    }
}
