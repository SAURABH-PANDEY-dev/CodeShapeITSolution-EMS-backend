package com.codeshape.expenses.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login request payload.
 */
@Data
public class LoginRequest {

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}