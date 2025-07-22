package com.ssctech.vendingmachine.model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoinBox {
    private final Map<CoinTender, Integer> coins;
    
    public CoinBox() {
        this.coins = new EnumMap<>(CoinTender.class);
        initializeWithStartingCoins();
    }
    
    private void initializeWithStartingCoins() {
        // Initialize with some coins
        coins.put(CoinTender.FIVE_PENCE, 20);
        coins.put(CoinTender.TEN_PENCE, 20);
        coins.put(CoinTender.TWENTY_PENCE, 15);
        coins.put(CoinTender.FIFTY_PENCE, 10);
        coins.put(CoinTender.ONE_POUND, 10);
        coins.put(CoinTender.TWO_POUND, 5);
    }
    
    private CoinBox(Map<CoinTender, Integer> coins) {
        this.coins = new EnumMap<>(coins);
    }
    
    public CoinBox addCoin(CoinTender coinTender) {
        Map<CoinTender, Integer> newCoins = new EnumMap<>(coins);
        newCoins.merge(coinTender, 1, Integer::sum);
        return new CoinBox(newCoins);
    }
    
    public CoinBox addCoins(List<CoinTender> coinsToAdd) {
        CoinBox result = this;
        for (CoinTender coinTender : coinsToAdd) {
            result = result.addCoin(coinTender);
        }
        return result;
    }
    
    public Optional<CoinBox> removeCoins(List<CoinTender> coinsToRemove) {
        Map<CoinTender, Integer> newCoins = new EnumMap<>(coins);
        
        // Check if we have enough coins
        Map<CoinTender, Long> requiredCounts = coinsToRemove.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        for (Map.Entry<CoinTender, Long> entry : requiredCounts.entrySet()) {
            CoinTender coinTender = entry.getKey();
            int required = entry.getValue().intValue();
            int available = newCoins.getOrDefault(coinTender, 0);
            
            if (available < required) {
                return Optional.empty();
            }
        }
        
        // Remove coins
        for (Map.Entry<CoinTender, Long> entry : requiredCounts.entrySet()) {
            CoinTender coinTender = entry.getKey();
            int required = entry.getValue().intValue();
            newCoins.merge(coinTender, -required, Integer::sum);
        }
        
        return Optional.of(new CoinBox(newCoins));
    }
    
    public Map<CoinTender, Integer> getCoins() {
        return new EnumMap<>(coins);
    }
    
    public Money getTotalValue() {
        BigDecimal total = coins.entrySet().stream()
                .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.of(total);
    }
}