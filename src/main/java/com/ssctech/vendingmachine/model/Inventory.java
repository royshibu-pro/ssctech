package com.ssctech.vendingmachine.model;

import com.ssctech.vendingmachine.exception.VendingException;
import java.util.EnumMap;
import java.util.Map;

public class Inventory {
    private final Map<Product, InventoryItem> items;

    public Inventory() {
        this.items = new EnumMap<>(Product.class);
        initializeDefaultStock();
    }

    private Inventory(Map<Product, InventoryItem> items) {
        this.items = new EnumMap<>(items);
    }

    private void initializeDefaultStock() {
        items.put(Product.COKE, new InventoryItem(Product.COKE, 10));
        items.put(Product.PEPSI, new InventoryItem(Product.PEPSI, 10));
        items.put(Product.WATER, new InventoryItem(Product.WATER, 15));
    }

    public boolean isAvailable(Product product) {
        return items.get(product).isAvailable();
    }

    public Inventory dispenseProduct(Product product) {
        InventoryItem item = items.get(product);
        if (!item.isAvailable()) {
            throw new VendingException("Product out of stock: " + product.getName());
        }
        Map<Product, InventoryItem> newItems = new EnumMap<>(items);
        newItems.put(product, item.decreaseStock());
        return new Inventory(newItems);
    }

    public void restockProduct(Product product, int amount) {
        items.computeIfPresent(product, (k, v) -> v.increaseStock(amount));
    }

    public int getStock(Product product) {
        return items.get(product).stock();
    }

    public Map<Product, InventoryItem> getItems() {
        return items;
    }
}