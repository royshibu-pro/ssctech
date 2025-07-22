package com.ssctech.vendingmachine.model;

import com.ssctech.vendingmachine.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coin Bank Tests")
class CoinTenderBoxTest {
    
    @Test
    @DisplayName("Should initialize with starting coins")
    void shouldInitializeWithStartingCoins() {
        CoinBox bank = new CoinBox();
        assertTrue(bank.getTotalValue().isGreaterThan(Money.zero()));
        assertFalse(bank.getCoins().isEmpty());
    }
    
    @Test
    @DisplayName("Should add single coin")
    void shouldAddSingleCoin() {
        CoinBox bank = new CoinBox();
        CoinBox newBank = bank.addCoin(CoinTender.ONE_POUND);
        
        int originalCount = bank.getCoins().getOrDefault(CoinTender.ONE_POUND, 0);
        int newCount = newBank.getCoins().getOrDefault(CoinTender.ONE_POUND, 0);
        
        assertEquals(originalCount + 1, newCount);
    }
    
    @Test
    @DisplayName("Should add multiple coins")
    void shouldAddMultipleCoins() {
        CoinBox bank = new CoinBox();
        List<CoinTender> coinsToAdd = TestDataBuilder.coinList(
            CoinTender.ONE_POUND, CoinTender.FIFTY_PENCE, CoinTender.ONE_POUND
        );
        
        CoinBox newBank = bank.addCoins(coinsToAdd);
        
        int originalCoinCount = bank.getCoins().getOrDefault(CoinTender.ONE_POUND, 0);
        int newCoinCount = newBank.getCoins().getOrDefault(CoinTender.ONE_POUND, 0);
        
        assertEquals(originalCoinCount + 2, newCoinCount);
    }
    
    @Test
    @DisplayName("Should remove coins when available")
    void shouldRemoveCoinsWhenAvailable() {
        CoinBox bank = new CoinBox();
        List<CoinTender> coinsToRemove = TestDataBuilder.coinList(CoinTender.FIVE_PENCE);
        
        Optional<CoinBox> result = bank.removeCoins(coinsToRemove);
        assertTrue(result.isPresent());
        
        int originalCount = bank.getCoins().getOrDefault(CoinTender.FIVE_PENCE, 0);
        int newCount = result.get().getCoins().getOrDefault(CoinTender.FIVE_PENCE, 0);
        
        assertEquals(originalCount - 1, newCount);
    }
    
    @Test
    @DisplayName("Should fail to remove coins when not available")
    void shouldFailToRemoveCoinsWhenNotAvailable() {
        CoinBox bank = new CoinBox();
        // Try to remove more coins than available
        List<CoinTender> coinsToRemove = Collections.nCopies(1000, CoinTender.TWO_POUND);
        
        Optional<CoinBox> result = bank.removeCoins(coinsToRemove);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should calculate total value correctly")
    void shouldCalculateTotalValueCorrectly() {
        CoinBox bank = new CoinBox();
        Money totalValue = bank.getTotalValue();
        
        // Calculate expected total
        BigDecimal expected = bank.getCoins().entrySet().stream()
                .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertEquals(Money.of(expected), totalValue);
    }
}