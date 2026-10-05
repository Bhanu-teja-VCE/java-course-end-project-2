package com.cpl.exception;

/**
 * Thrown when a bid is submitted for a lot that is already closed or sold.
 * // Unit II: Custom Exception Hierarchy
 */
public class AuctionClosedException extends AuctionException {
    private static final long serialVersionUID = 1L;

    public AuctionClosedException(String message) {
        super(message);
    }
}
