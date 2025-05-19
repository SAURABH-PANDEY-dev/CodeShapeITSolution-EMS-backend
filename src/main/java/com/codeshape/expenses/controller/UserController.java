package com.codeshape.expenses.controller;

import com.codeshape.expenses.dto.JwtResponse;
import com.codeshape.expenses.dto.RefreshTokenRequest;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import com.codeshape.expenses.dto.LoginRequest;
import com.codeshape.expenses.dto.JwtResponse;

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
}