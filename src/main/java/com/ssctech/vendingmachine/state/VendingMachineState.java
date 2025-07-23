package com.ssctech.vendingmachine.state;

import com.ssctech.vendingmachine.model.CoinBox;
import com.ssctech.vendingmachine.model.Inventory;

public class VendingMachineState {
    private final Inventory inventory;
    private final CoinBox coinBox;
    private final TransactionState transaction;

    public VendingMachineState(Inventory inventory, CoinBox coinBox, TransactionState state) {
        this.coinBox = coinBox;
        this.transaction = state;
        this.inventory = inventory;
    }

    public static VendingMachineState initialize() {
        return new VendingMachineState(
                new Inventory(),
                new CoinBox(),
                TransactionState.empty()
        );
    }

    public VendingMachineState withInventory(Inventory newInventory) {
        return new VendingMachineState(newInventory, coinBox, transaction);
    }

    public VendingMachineState withCoinBox(CoinBox newCoinBox) {
        return new VendingMachineState(inventory, newCoinBox, transaction);
    }

    public VendingMachineState withTransaction(TransactionState newTransaction) {
        return new VendingMachineState(inventory, coinBox, newTransaction);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public CoinBox getCoinBox() {
        return coinBox;
    }

    public TransactionState getTransaction() {
        return transaction;
    }
}