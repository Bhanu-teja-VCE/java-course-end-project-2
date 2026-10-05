package com.cpl.exception;

/**
 * Base checked exception for the Campus Premier League application.
 * // Unit II: Own exception subclasses
 */
public class CplException extends Exception {
    private static final long serialVersionUID = 1L;

    public CplException(String message) {
        super(message);
    }

    public CplException(String message, Throwable cause) {
        super(message, cause);
    }
}
