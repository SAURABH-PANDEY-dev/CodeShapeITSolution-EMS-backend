package com.codeshape.expenses.controller;

import com.codeshape.expenses.dto.AdminDashboardStatsDTO;
import com.codeshape.expenses.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public AdminDashboardStatsDTO getDashboardStats() {
        return adminDashboardService.getDashboardStats();
    }
}
