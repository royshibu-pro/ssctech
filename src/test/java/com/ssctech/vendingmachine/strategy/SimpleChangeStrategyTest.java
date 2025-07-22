package com.ssctech.vendingmachine.strategy;

import com.ssctech.vendingmachine.TestDataBuilder;
import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Simple Change Strategy Tests")
class SimpleChangeStrategyTest {
    
    private SimpleChangeStrategy strategy;
    private Map<CoinTender, Integer> availableCoins;
    
    @BeforeEach
    void setUp() {
        strategy = new SimpleChangeStrategy();
        availableCoins = TestDataBuilder.coinMap(
            CoinTender.TWO_POUND, 5,
            CoinTender.ONE_POUND, 10,
            CoinTender.FIFTY_PENCE, 10,
            CoinTender.TWENTY_PENCE, 15,
            CoinTender.TEN_PENCE, 20,
            CoinTender.FIVE_PENCE, 20
        );
    }
    
    @ParameterizedTest
    @DisplayName("Should make change for valid amounts")
    @ValueSource(strings = {"0.05", "0.15", "0.85", "1.50", "2.75"})
    void shouldMakeChangeForValidAmounts(String amountStr) {
        Money amount = TestDataBuilder.money(amountStr);
        
        Optional<List<CoinTender>> result = strategy.makeChange(amount, availableCoins);
        
        assertTrue(result.isPresent());
        
        // Verify the change adds up correctly
        BigDecimal total = result.get().stream()
                .map(CoinTender::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertEquals(amount.amount(), total);
    }
    
    @Test
    @DisplayName("Should prefer larger denominations")
    void shouldPreferLargerDenominations() {
        Money amount = TestDataBuilder.money("2.00");
        
        Optional<List<CoinTender>> result = strategy.makeChange(amount, availableCoins);
        
        assertTrue(result.isPresent());
        assertTrue(result.get().contains(CoinTender.TWO_POUND));
        assertEquals(1, result.get().size());
    }
    
    @Test
    @DisplayName("Should fail when exact change cannot be made")
    void shouldFailWhenExactChangeCannotBeMade() {
        Map<CoinTender, Integer> limitedCoins = TestDataBuilder.coinMap(
            CoinTender.TWENTY_PENCE, 1
        );
        
        Money amount = TestDataBuilder.money("0.15");
        
        Optional<List<CoinTender>> result = strategy.makeChange(amount, limitedCoins);
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle zero change")
    void shouldHandleZeroChange() {
        Money amount = Money.zero();
        
        Optional<List<CoinTender>> result = strategy.makeChange(amount, availableCoins);
        
        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());
    }
}