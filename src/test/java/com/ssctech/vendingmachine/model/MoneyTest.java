package com.ssctech.vendingmachine.model;

import com.ssctech.vendingmachine.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {
    
    @Test
    @DisplayName("Should create money with valid amount")
    void shouldCreateMoneyWithValidAmount() {
        Money money = TestDataBuilder.money("1.50");
        assertEquals(new BigDecimal("1.50"), money.amount());
    }
    
    @Test
    @DisplayName("Should throw exception for negative amount")
    void shouldThrowExceptionForNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> 
            TestDataBuilder.money("-1.00"));
    }
    
    @Test
    @DisplayName("Should round to 2 decimal places")
    void shouldRoundToTwoDecimalPlaces() {
        Money money = Money.of(new BigDecimal("1.999"));
        assertEquals(new BigDecimal("2.00"), money.amount());
    }
    
    @ParameterizedTest
    @DisplayName("Should add money correctly")
    @CsvSource({
        "1.50, 2.50, 4.00",
        "0.05, 0.05, 0.10",
        "1.45, 0.55, 2.00"
    })
    void shouldAddMoneyCorrectly(String amount1, String amount2, String expected) {
        Money money1 = TestDataBuilder.money(amount1);
        Money money2 = TestDataBuilder.money(amount2);
        Money result = money1.add(money2);
        
        assertEquals(TestDataBuilder.money(expected), result);
    }
    
    @ParameterizedTest
    @DisplayName("Should subtract money correctly")
    @CsvSource({
        "2.50, 1.50, 1.00",
        "1.00, 0.50, 0.50",
        "2.00, 2.00, 0.00"
    })
    void shouldSubtractMoneyCorrectly(String amount1, String amount2, String expected) {
        Money money1 = TestDataBuilder.money(amount1);
        Money money2 = TestDataBuilder.money(amount2);
        Money result = money1.subtract(money2);
        
        assertEquals(TestDataBuilder.money(expected), result);
    }
    
    @ParameterizedTest
    @DisplayName("Should compare money amounts correctly")
    @CsvSource({
        "2.00, 1.50, true, true",
        "1.50, 1.50, true, false",
        "1.00, 1.50, false, false"
    })
    void shouldCompareMoneyAmountsCorrectly(String amount1, String amount2, 
                                          boolean expectedGTE, boolean expectedGT) {
        Money money1 = TestDataBuilder.money(amount1);
        Money money2 = TestDataBuilder.money(amount2);
        
        assertEquals(expectedGTE, money1.isGreaterThanOrEqual(money2));
        assertEquals(expectedGT, money1.isGreaterThan(money2));
    }
    
    @Test
    @DisplayName("Should create zero money")
    void shouldCreateZeroMoney() {
        Money zero = Money.zero();
        assertEquals(BigDecimal.ZERO.setScale(2), zero.amount());
    }
    
    @Test
    @DisplayName("Should format money as string")
    void shouldFormatMoneyAsString() {
        Money money = TestDataBuilder.money("1.50");
        assertEquals("£1.50", money.toString());
    }
}