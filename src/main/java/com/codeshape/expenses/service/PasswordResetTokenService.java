package com.codeshape.expenses.service;

import com.codeshape.expenses.model.PasswordResetToken;
import com.codeshape.expenses.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenService {

    PasswordResetToken createToken(User user);

    Optional<PasswordResetToken> findByToken(String token);

    void deleteToken(PasswordResetToken token);

    boolean isTokenExpired(PasswordResetToken token);
}
