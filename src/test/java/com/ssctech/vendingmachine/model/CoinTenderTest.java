package com.ssctech.vendingmachine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coin Enum Tests")
class CoinTenderTest {
    
    @ParameterizedTest
    @DisplayName("Should return correct coin values")
    @EnumSource(CoinTender.class)
    void shouldReturnCorrectCoinValues(CoinTender coinTender) {
        assertNotNull(coinTender.getValue());
        assertTrue(coinTender.getValue().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @ParameterizedTest
    @DisplayName("Should find coin by value")
    @CsvSource({
        "0.05, FIVE_PENCE",
        "0.10, TEN_PENCE",
        "0.20, TWENTY_PENCE",
        "0.50, FIFTY_PENCE",
        "1.00, ONE_POUND",
        "2.00, TWO_POUND"
    })
    void shouldFindCoinByValue(String value, String expectedCoin) {
        Optional<CoinTender> coin = CoinTender.fromValue(new BigDecimal(value));
        assertTrue(coin.isPresent());
        assertEquals(CoinTender.valueOf(expectedCoin), coin.get());
    }
    
    @Test
    @DisplayName("Should return empty for invalid coin value")
    void shouldReturnEmptyForInvalidCoinValue() {
        Optional<CoinTender> coin = CoinTender.fromValue(new BigDecimal("0.03"));
        assertTrue(coin.isEmpty());
    }
}