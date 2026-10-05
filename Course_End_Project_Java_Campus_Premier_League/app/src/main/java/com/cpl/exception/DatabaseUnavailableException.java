package com.cpl.exception;

/**
 * Thrown when the MySQL database is unreachable or connection fails.
 * Used to trigger graceful fallback to offline in-memory storage.
 * // Unit II: Custom Exception Hierarchy
 */
public class DatabaseUnavailableException extends CplException {
    private static final long serialVersionUID = 1L;

    public DatabaseUnavailableException(String message) {
        super(message);
    }

    public DatabaseUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
