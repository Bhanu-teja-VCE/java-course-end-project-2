package com.cpl.exception;

/**
 * Unchecked runtime exception thrown when match simulation enters an inconsistent state.
 * // Unit II: Built-in & Unchecked exceptions
 */
public class IllegalMatchStateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IllegalMatchStateException(String message) {
        super(message);
    }
}
