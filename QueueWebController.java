package com.pascs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/queue")
public class QueueWebController {

    @GetMapping("")
    public String viewQueue() {
        return "staff/queue";
    }
}
