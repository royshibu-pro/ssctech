package com.ssctech.vendingmachine;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;
import com.ssctech.vendingmachine.state.TransactionState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class TestDataBuilder {
    public static Money money(String amount) {
        return Money.of(new BigDecimal(amount));
    }
    
    public static Money money(double amount) {
        return Money.of(BigDecimal.valueOf(amount));
    }
    
    public static List<CoinTender> coinList(CoinTender... coinTenders) {
        return Arrays.asList(coinTenders);
    }
    
    public static Map<CoinTender, Integer> coinMap(Object... pairs) {
        Map<CoinTender, Integer> map = new EnumMap<>(CoinTender.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((CoinTender) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }
}