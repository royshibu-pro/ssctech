package com.ssctech.vendingmachine.state;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record TransactionState(
        Money insertedAmount,
        List<CoinTender> insertedCoinTenders
        // todo add time for audit?
) {
    public static TransactionState empty() {
        return new TransactionState(Money.zero(), new ArrayList<>());
    }

    public TransactionState addCoin(CoinTender coinTender) {
        List<CoinTender> newCoinTenders = new ArrayList<>(insertedCoinTenders);
        newCoinTenders.add(coinTender);
        return new TransactionState(
                insertedAmount.add(Money.of(coinTender.getValue())),
                newCoinTenders
        );
    }
}