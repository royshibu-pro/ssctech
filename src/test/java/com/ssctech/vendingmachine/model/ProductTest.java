package com.ssctech.vendingmachine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Product Enum Tests")
class ProductTest {
    
    @ParameterizedTest
    @DisplayName("Should have correct product properties")
    @CsvSource({
        "COKE, Coke, 1.50",
        "PEPSI, Pepsi, 1.45",
        "WATER, Water, 0.90"
    })
    void shouldHaveCorrectProductProperties(String productName, String displayName, String price) {
        Product product = Product.valueOf(productName);
        assertEquals(displayName, product.getName());
        assertEquals(new BigDecimal(price), product.getPrice());
    }
}