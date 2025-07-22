package com.ssctech.vendingmachine.model;

import java.math.BigDecimal;

public enum Product {
    COKE("Coke", new BigDecimal("1.50")),
    PEPSI("Pepsi", new BigDecimal("1.45")),
    WATER("Water", new BigDecimal("0.90"));

    private final String name;
    private final BigDecimal price;

    Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}