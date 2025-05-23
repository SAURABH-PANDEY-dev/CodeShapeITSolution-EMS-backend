package com.codeshape.expenses.controller;

import com.codeshape.expenses.dto.*;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST Controller for User-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create new user
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.saveUser(user);
        return ResponseEntity.ok(created);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = userService.login(request);
        return ResponseEntity.ok(jwtResponse);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getFullName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        return ResponseEntity.ok(userInfo);
    }
    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody PasswordResetRequest request,
                                                       HttpServletRequest servletRequest) {
        String resetLink = servletRequest.getRequestURL().toString().replace(
                servletRequest.getServletPath(), "/reset-password"
        );
        userService.initiatePasswordReset(request.getEmail(), resetLink);
        return ResponseEntity.ok("Password reset link sent to your email if it exists.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok("Password changed successfully");
    }


    // Deactivate a user by ID (Admin only)
    @PutMapping("/admin/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    // Activate a user by ID (Admin only)
    @PutMapping("/admin/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated successfully");
    }


}