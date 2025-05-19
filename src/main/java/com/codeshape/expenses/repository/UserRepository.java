package com.codeshape.expenses.repository;

import com.codeshape.expenses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used in login/authentication)
    Optional<User> findByEmail(String email);

    // Check if a user with email already exists
    boolean existsByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}