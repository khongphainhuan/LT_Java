//api/test/
package com.pascs.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    // API 1: Public - Ai cũng truy cập được
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content - Ai cũng có thể truy cập được!";
    }
    //API 2: Chỉ CITIZEN(công dân) truy cập được
    @GetMapping("/citizen")
    @PreAuthorize("hasRole('CITIZEN')")
    public String citizenAccess() {
        return "Nội dung dành cho Công dân!";
    }
    //API 3: Chỉ STAFF(cán bộ) truy cập được
    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public String staffAccess() {
        return "Nội dung dành cho Cán bộ!";
    }
    //API 4: Chỉ ADMIN(quản trị viên) truy cập được
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Nội dung dành cho Quản trị viên!";
    }
    //API 5: STAFF & ADMIN cùng truy cập được
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public String staffDashboard() {
        return "Dashboard dành cho Cán bộ & Quản trị viên!";
    }
}