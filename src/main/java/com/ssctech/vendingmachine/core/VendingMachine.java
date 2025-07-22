package com.ssctech.vendingmachine.core;

import com.ssctech.vendingmachine.exception.VendingException;
import com.ssctech.vendingmachine.model.*;
import com.ssctech.vendingmachine.result.PurchaseResult;
import com.ssctech.vendingmachine.result.RefundResult;
import com.ssctech.vendingmachine.state.TransactionState;
import com.ssctech.vendingmachine.state.VendingMachineState;
import com.ssctech.vendingmachine.strategy.ChangeStrategy;
import com.ssctech.vendingmachine.strategy.SimpleChangeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendingMachine {
    private VendingMachineState state;
    private final ChangeStrategy changeStrategy;

    public VendingMachine() {
        this.state = VendingMachineState.initialize();
        this.changeStrategy = new SimpleChangeStrategy();
    }

    public void insertCoin(CoinTender coinTender) {
        TransactionState newTransaction = this.state.getTransaction().addCoin(coinTender);
        CoinBox newCoinBox = this.state.getCoinBank().addCoin(coinTender);

        VendingMachineState newState = this.state.withTransaction(newTransaction)
                .withCoinBank(newCoinBox);

        this.state = newState;
        System.out.printf("Coin inserted: %s, Total: %s%n", coinTender, newTransaction.insertedAmount());
    }

    public PurchaseResult selectProduct(Product product) {
        if (!this.state.getInventory().isAvailable(product)) {
            throw new VendingException("Product out of stock: " + product.getName());
        }

        Money productPrice = Money.of(product.getPrice());
        Money insertedAmount = this.state.getTransaction().insertedAmount();

        if (!insertedAmount.isGreaterThanOrEqual(productPrice)) {
            Money needed = productPrice.subtract(insertedAmount);
            throw new VendingException("Insufficient funds. Need " + needed + " more");
        }

        Money changeAmount = insertedAmount.subtract(productPrice);
        List<CoinTender> changeCoinTenders = new ArrayList<>();
        if (changeAmount.isGreaterThan(Money.zero())) {
            Optional<List<CoinTender>> changeOpt = changeStrategy.makeChange(changeAmount, this.state.getCoinBank().getCoins());
            if (changeOpt.isEmpty()) {
                throw new VendingException("Cannot make exact change");
            }
            changeCoinTenders = changeOpt.get();
        }

        Inventory newInventory = this.state.getInventory().dispenseProduct(product);
        Optional<CoinBox> newCoinBankOpt = this.state.getCoinBank().removeCoins(changeCoinTenders);
        if (newCoinBankOpt.isEmpty()) {
            throw new VendingException("Cannot dispense change due to coin bank error");
        }

        this.state = this.state
                .withInventory(newInventory)
                .withCoinBank(newCoinBankOpt.get())
                .withTransaction(TransactionState.empty());

        System.out.printf("Product dispensed: %s%n", product.getName());
        if (changeAmount.isGreaterThan(Money.zero())) {
            System.out.printf("Change: %s %s", changeAmount, changeCoinTenders);
        }

        return new PurchaseResult(product, changeAmount, changeCoinTenders);
    }

    public RefundResult cancelTransaction() {
        TransactionState transaction = this.state.getTransaction();

        if (transaction.insertedAmount().equals(Money.zero())) {
            System.out.println("No transaction to cancel");
            return new RefundResult(Money.zero(), new ArrayList<>());
        }

        Money refundAmount = transaction.insertedAmount();
        List<CoinTender> refundCoinTenders = transaction.insertedCoinTenders();

        this.state = this.state.withTransaction(TransactionState.empty());
        System.out.printf("Transaction cancelled. Refund: %s%n", refundAmount);
        return new RefundResult(refundAmount, refundCoinTenders);
    }

    public void reset() {
        state = VendingMachineState.initialize();
        System.out.println("Vending machine has been reset");
    }

    public VendingMachineState getCurrentState() {
        return state;
    }
}