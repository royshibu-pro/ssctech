package com.ssctech.vendingmachine.strategy;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;

import java.math.BigDecimal;
import java.util.*;

public class SimpleChangeStrategy implements ChangeStrategy {
    @Override
    public Optional<List<CoinTender>> makeChange(Money amount, Map<CoinTender, Integer> availableCoins) {
        List<CoinTender> result = new ArrayList<>();
        BigDecimal remaining = amount.amount();
        
        List<CoinTender> sortedCoinTenders = Arrays.stream(CoinTender.values())
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .toList();
        
        for (CoinTender coinTender : sortedCoinTenders) {
            int availableCount = availableCoins.getOrDefault(coinTender, 0);
            int neededCount = remaining.divideToIntegralValue(coinTender.getValue()).intValue();
            int useCount = Math.min(availableCount, neededCount);
            
            for (int i = 0; i < useCount; i++) {
                result.add(coinTender);
                remaining = remaining.subtract(coinTender.getValue());
            }
            
            if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
        
        return remaining.compareTo(BigDecimal.ZERO) == 0 
            ? Optional.of(result) 
            : Optional.empty();
    }
}