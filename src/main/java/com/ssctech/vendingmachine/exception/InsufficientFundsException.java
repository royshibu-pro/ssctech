package com.ssctech.vendingmachine.exception;

public class InsufficientFundsException extends VendingException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
