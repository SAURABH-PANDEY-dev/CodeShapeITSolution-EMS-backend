package com.codeshape.expenses.exception;

/**
 * Thrown when an email is already in use.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}