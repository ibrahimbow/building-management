package com.why.buildingmanagement.auth.infrastructure.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    @GetMapping("/dashboard")
    public String tenantDashboard() {
        return "Tenant Access Granted";
    }
}