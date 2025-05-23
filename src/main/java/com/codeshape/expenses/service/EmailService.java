package com.codeshape.expenses.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);
}
