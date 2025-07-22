package com.ssctech.vendingmachine.model;

public record InventoryItem(Product product, int stock) {
    public InventoryItem decreaseStock() {
        if (stock <= 0) {
            throw new IllegalStateException("Stock cannot be below zero");
        }
        return new InventoryItem(product, stock - 1);
    }

    public InventoryItem increaseStock(int amount) {
        return new InventoryItem(product, stock + amount);
    }

    public boolean isAvailable() {
        return stock > 0;
    }
}