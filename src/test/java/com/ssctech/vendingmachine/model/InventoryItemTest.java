package com.ssctech.vendingmachine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Item Tests")
class InventoryItemTest {
    
    @Test
    @DisplayName("Should create inventory item")
    void shouldCreateInventoryItem() {
        InventoryItem item = new InventoryItem(Product.COKE, 10);
        assertEquals(Product.COKE, item.product());
        assertEquals(10, item.stock());
        assertTrue(item.isAvailable());
    }
    
    @Test
    @DisplayName("Should decrease stock")
    void shouldDecreaseStock() {
        InventoryItem item = new InventoryItem(Product.COKE, 5);
        InventoryItem newItem = item.decreaseStock();
        
        assertEquals(4, newItem.stock());
        assertTrue(newItem.isAvailable());
    }
    
    @Test
    @DisplayName("Should increase stock")
    void shouldIncreaseStock() {
        InventoryItem item = new InventoryItem(Product.COKE, 5);
        InventoryItem newItem = item.increaseStock(3);
        
        assertEquals(8, newItem.stock());
    }
    
    @Test
    @DisplayName("Should throw exception when decreasing stock below zero")
    void shouldThrowExceptionWhenDecreasingStockBelowZero() {
        InventoryItem item = new InventoryItem(Product.COKE, 0);
        assertThrows(IllegalStateException.class, item::decreaseStock);
    }
    
    @Test
    @DisplayName("Should not be available when stock is zero")
    void shouldNotBeAvailableWhenStockIsZero() {
        InventoryItem item = new InventoryItem(Product.COKE, 0);
        assertFalse(item.isAvailable());
    }
}