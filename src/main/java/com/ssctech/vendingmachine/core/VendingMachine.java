package com.ssctech.vendingmachine.core;

import com.ssctech.vendingmachine.audit.AuditLog;
import com.ssctech.vendingmachine.event.VendingEvent;
import com.ssctech.vendingmachine.exception.ChangeNotAvailableException;
import com.ssctech.vendingmachine.exception.InsufficientFundsException;
import com.ssctech.vendingmachine.exception.ProductOutOfStockException;
import com.ssctech.vendingmachine.exception.VendingException;
import com.ssctech.vendingmachine.model.*;
import com.ssctech.vendingmachine.result.PurchaseResult;
import com.ssctech.vendingmachine.result.RefundResult;
import com.ssctech.vendingmachine.state.TransactionState;
import com.ssctech.vendingmachine.state.VendingMachineState;
import com.ssctech.vendingmachine.strategy.ChangeStrategy;
import com.ssctech.vendingmachine.strategy.SimpleChangeStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendingMachine {
    private VendingMachineState state;
    private final ChangeStrategy changeStrategy;
    private final AuditLog auditLog;

    public VendingMachine() {
        this.state = VendingMachineState.initialize();
        this.changeStrategy = new SimpleChangeStrategy();
        this.auditLog = new AuditLog();
    }

    public VendingMachine(ChangeStrategy changeStrategy, AuditLog auditLog) {
        this.state = VendingMachineState.initialize();
        this.changeStrategy = changeStrategy;
        this.auditLog = auditLog;
    }


    public void insertCoin(CoinTender coinTender) {
        // adds the coin to the transaction
        TransactionState newTransaction = this.state.getTransaction().addCoin(coinTender);
        this.state = this.state.withTransaction(newTransaction);

                auditLog.addEvent(new VendingEvent.CoinInserted(coinTender, newTransaction.insertedAmount(), LocalDateTime.now()));
        System.out.printf("Coin inserted: %s, Total: %s%n", coinTender, newTransaction.insertedAmount());
    }

    public PurchaseResult selectProduct(Product product) {
        auditLog.addEvent(new VendingEvent.ProductSelected(product, LocalDateTime.now()));
        if (!this.state.getInventory().isAvailable(product)) {
            throw new ProductOutOfStockException("Product out of stock: " + product.getName());
        }

        Money productPrice = Money.of(product.getPrice());
        Money insertedAmount = this.state.getTransaction().insertedAmount();

        if (!insertedAmount.isGreaterThanOrEqual(productPrice)) {
            Money needed = productPrice.subtract(insertedAmount);
            throw new InsufficientFundsException("Insufficient funds. Need " + needed + " more");
        }

        Money changeAmount = insertedAmount.subtract(productPrice);
        // determine the change coins
        List<CoinTender> changeCoinTenders = new ArrayList<>();
        if (changeAmount.isGreaterThan(Money.zero())) {
            Optional<List<CoinTender>> changeOpt = changeStrategy.makeChange(changeAmount, this.state.getCoinBox().getCoins());
            if (changeOpt.isEmpty()) {
                throw new ChangeNotAvailableException("Cannot make exact change");
            }
            changeCoinTenders = changeOpt.get();
        }
        // dispense the product
        Inventory newInventory = this.state.getInventory().dispenseProduct(product);
        // update coin box with the new coin count
        Optional<CoinBox> newCoinBoxOpt = this.state.getCoinBox().removeCoins(changeCoinTenders);
        if (newCoinBoxOpt.isEmpty()) {
            throw new VendingException("Cannot dispense change due to coin bank error");
        }

        // add the inserted coins and update the coin box
        List<CoinTender> addedCoins = this.state.getTransaction().insertedCoinTenders();
        CoinBox updatedCoinBox = newCoinBoxOpt.get().addCoins(addedCoins);

        // set new state with currently available coin and inventory
        this.state = this.state
                .withInventory(newInventory)
                .withCoinBox(updatedCoinBox)
                .withTransaction(TransactionState.empty());

        auditLog.addEvent(new VendingEvent.ProductDispensed(product, changeAmount,LocalDateTime.now()));
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

        // reset the state with a fresh transactions state
        this.state = this.state
                .withTransaction(TransactionState.empty());

        System.out.printf("Transaction cancelled. Refund: %s%n", refundAmount);
        auditLog.addEvent(new VendingEvent.TransactionCancelled(refundAmount, LocalDateTime.now()));
        return new RefundResult(refundAmount, refundCoinTenders);
    }

    public void reset() {
        state = VendingMachineState.initialize();
        auditLog.addEvent(new VendingEvent.MachineReset(LocalDateTime.now()));
        System.out.println("Vending machine has been reset");
    }

    public VendingMachineState getCurrentState() {
        return state;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }
}