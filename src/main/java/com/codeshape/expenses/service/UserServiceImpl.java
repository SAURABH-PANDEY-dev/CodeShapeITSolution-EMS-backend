package com.codeshape.expenses.service;

import com.codeshape.expenses.dto.RefreshTokenRequest;
import com.codeshape.expenses.exception.DuplicateEmailException;
import com.codeshape.expenses.model.PasswordResetToken;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.codeshape.expenses.repository.PasswordResetTokenRepository;
import com.codeshape.expenses.service.EmailService;
import org.springframework.web.bind.annotation.RequestParam;



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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
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

    @Override
    public void initiatePasswordReset(String email, String resetLink) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("No user with this email exists");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(passwordResetToken);

        String fullResetLink = resetLink + "?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), fullResetLink);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Optionally: delete token after use
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(User.Role.EMPLOYEE); // or another logic to deactivate e.g. a boolean flag if available
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(User.Role.EMPLOYEE); // or restore previous role
        userRepository.save(user);
    }



}