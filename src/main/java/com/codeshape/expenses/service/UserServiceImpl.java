package com.codeshape.expenses.service;

import com.codeshape.expenses.dto.RefreshTokenRequest;
import com.codeshape.expenses.exception.DuplicateEmailException;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.codeshape.expenses.dto.LoginRequest;
import com.codeshape.expenses.exception.DuplicateEmailException;
import com.codeshape.expenses.dto.JwtResponse;
import com.codeshape.expenses.security.JwtService;
/**
 * Implementation class for UserService.
 * Handles business logic related to User operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Constructor injection (preferred way)
    @Autowired
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtService = new JwtService(); // manually inject here for now
    }
    @Override
    public User saveUser(User user) {
        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email is already registered");
        }

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(User.Role.EMPLOYEE);
        }

        // Encrypt password and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not registered"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = UUID.randomUUID().toString();

        // Save refresh token in DB
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new JwtResponse(accessToken, refreshToken,user.getId());
    }
    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new JwtResponse(newAccessToken, user.getRefreshToken(),user.getId());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
}