package com.ssctech.vendingmachine.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

public enum CoinTender {
    FIVE_PENCE(new BigDecimal("0.05")),
    TEN_PENCE(new BigDecimal("0.10")),
    TWENTY_PENCE(new BigDecimal("0.20")),
    FIFTY_PENCE(new BigDecimal("0.50")),
    ONE_POUND(new BigDecimal("1.00")),
    TWO_POUND(new BigDecimal("2.00"));

    private final BigDecimal value;

    CoinTender(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public static Optional<CoinTender> fromValue(BigDecimal value) {
        return Arrays.stream(values())
                .filter(coin -> coin.value.compareTo(value) == 0)
                .findFirst();
    }
}