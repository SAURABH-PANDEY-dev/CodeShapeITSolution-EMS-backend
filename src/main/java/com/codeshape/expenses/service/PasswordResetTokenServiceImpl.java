package com.codeshape.expenses.service;

import com.codeshape.expenses.model.PasswordResetToken;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.repository.PasswordResetTokenRepository;
import com.codeshape.expenses.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public PasswordResetToken createToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // token valid for 24h
        return tokenRepository.save(resetToken);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }

    @Override
    public boolean isTokenExpired(PasswordResetToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
