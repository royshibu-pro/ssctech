package com.ssctech.vendingmachine.model;

import com.ssctech.vendingmachine.exception.VendingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Tests")
class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
    }

    @ParameterizedTest
    @DisplayName("Should have initial stock for all products")
    @EnumSource(Product.class)
    void shouldHaveInitialStockForAllProducts(Product product) {
        assertTrue(inventory.isAvailable(product));
        assertTrue(inventory.getStock(product) > 0);
    }

    @Test
    @DisplayName("Should dispense product when available")
    void shouldDispenseProductWhenAvailable() {
        int initialStock = inventory.getStock(Product.COKE);

        Inventory newInventory = inventory.dispenseProduct(Product.COKE);

        assertEquals(initialStock - 1, newInventory.getStock(Product.COKE));
    }

    @Test
    @DisplayName("Should fail to dispense when out of stock")
    void shouldFailToDispenseWhenOutOfStock() {
        // out of stock
        Inventory oos = inventory;
        int initialStock = inventory.getStock(Product.WATER);
        for (int i = 0; i < initialStock; i++) {
            oos = oos.dispenseProduct(Product.WATER);
        }

        // Final oos inventory to test against
        final Inventory finalDepleted = oos;
        assertThrows(VendingException.class, () -> {
            finalDepleted.dispenseProduct(Product.WATER);
        });
    }

    @Test
    @DisplayName("Should restock product")
    void shouldRestockProduct() {
        int initialStock = inventory.getStock(Product.PEPSI);
        inventory.restockProduct(Product.PEPSI, 5);

        assertEquals(initialStock + 5, inventory.getStock(Product.PEPSI));
    }
}