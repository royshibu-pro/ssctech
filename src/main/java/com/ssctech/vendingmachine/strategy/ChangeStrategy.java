package com.ssctech.vendingmachine.strategy;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChangeStrategy {
    Optional<List<CoinTender>> makeChange(Money amount, Map<CoinTender, Integer> availableCoins);
}