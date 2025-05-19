package com.codeshape.expenses.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.minidev.json.annotate.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.codeshape.expenses.model.Expense;
import java.util.List;;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "refresh_token")
    private String refreshToken;

    public enum Role {
        EMPLOYEE,
        MANAGER,
        ADMIN
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion during JSON serialization
    private List<Expense> expenses;
}