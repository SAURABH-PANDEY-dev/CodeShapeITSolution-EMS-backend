package com.codeshape.expenses.service;

import com.codeshape.expenses.dto.JwtResponse;
import com.codeshape.expenses.dto.RefreshTokenRequest;
import com.codeshape.expenses.model.User;
import java.util.List;
import java.util.Optional;
import com.codeshape.expenses.dto.LoginRequest;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();
    JwtResponse login(LoginRequest request);
    JwtResponse refreshToken(RefreshTokenRequest request);
    User getUserById(Long id);
}