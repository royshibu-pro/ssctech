package com.ssctech.vendingmachine.exception;

public class ProductOutOfStockException extends VendingException{
    public ProductOutOfStockException(String message) {
        super(message);
    }
}
