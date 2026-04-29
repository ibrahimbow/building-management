package com.why.buildingmanagement.auth.infrastructure.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        return "Welcome to the Admin Dashboard!";
    }
}
