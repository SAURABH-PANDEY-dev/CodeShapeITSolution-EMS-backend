package com.codeshape.expenses.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for testing role-based access control.
 */
@RestController
@RequestMapping("/api")
public class TestRoleController {

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeAccess() {
        return "Hello Employee!";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String managerAccess() {
        return "Hello Manager or Admin!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Hello Admin!";
    }
}
// Manager token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW5hZ2VyQGV4YW1wbGUuY29tIiwicm9sZSI6Ik1BTkFHRVIiLCJpYXQiOjE3NDczNzc0MDAsImV4cCI6MTc0NzQ2MzgwMH0.CO9ePHNJHzh2GXu6yzpogXhX5ECntp5kds5DT-lXUhE
// Admin token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc0NzM3NzE5NSwiZXhwIjoxNzQ3NDYzNTk1fQ.XkwyfaBYB64tG56XYu8QXGeg6u97wYfDneynA8Ch12Y