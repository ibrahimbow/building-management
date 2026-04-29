package com.why.buildingmanagement.auth.infrastructure.api.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @GetMapping("/dashboard")
    public String managerDashboard() {
        return "Manager Access Granted";
    }
}