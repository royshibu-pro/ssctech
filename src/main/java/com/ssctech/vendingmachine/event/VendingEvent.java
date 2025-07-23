package com.ssctech.vendingmachine.event;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;
import com.ssctech.vendingmachine.model.Product;

import java.time.LocalDateTime;

public sealed interface VendingEvent permits
        VendingEvent.CoinInserted,
        VendingEvent.ProductSelected,
        VendingEvent.ProductDispensed,
        VendingEvent.TransactionCancelled,
        VendingEvent.MachineReset {

    record CoinInserted(CoinTender coinTender, Money totalAmount, LocalDateTime time) implements VendingEvent {
    }

    record ProductSelected(Product product, LocalDateTime time) implements VendingEvent {
    }

    record ProductDispensed(Product product, Money change, LocalDateTime time) implements VendingEvent {
    }

    record TransactionCancelled(Money refund, LocalDateTime time) implements VendingEvent {
    }

    record MachineReset(LocalDateTime time) implements VendingEvent {
    }
}
