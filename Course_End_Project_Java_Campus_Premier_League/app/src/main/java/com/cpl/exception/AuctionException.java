package com.cpl.exception;

/**
 * Base checked exception for auction-related validation failures.
 * // Unit II: Custom Exception Hierarchy
 */
public class AuctionException extends CplException {
    private static final long serialVersionUID = 1L;

    public AuctionException(String message) {
        super(message);
    }
}
