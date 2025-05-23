package com.codeshape.expenses.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;


@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotBlank(message = "Description must not be blank")
    @jakarta.validation.constraints.Size(max = 255, message = "Description must be less than 255 characters")
    private String description;


    @Column(nullable = false)
    @jakarta.validation.constraints.Positive(message = "Amount must be a positive value")
    private double amount;


    @Column(nullable = false)
    @jakarta.validation.constraints.PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;


    @Column(name = "file_name")
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"expenses", "password", "refreshToken"})
    private User user;

    public enum Category {
        TRAVEL, FOOD, OFFICE, OTHER
    }
}