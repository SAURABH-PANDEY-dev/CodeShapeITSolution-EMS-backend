package com.codeshape.expenses.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateExpenseRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Optional field for invoice filename (used if attached)
    private String fileName;
}
