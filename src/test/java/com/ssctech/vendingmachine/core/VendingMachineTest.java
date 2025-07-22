package com.ssctech.vendingmachine.core;

import com.ssctech.vendingmachine.TestDataBuilder;
import com.ssctech.vendingmachine.exception.VendingException;
import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;
import com.ssctech.vendingmachine.model.Product;
import com.ssctech.vendingmachine.result.PurchaseResult;
import com.ssctech.vendingmachine.result.RefundResult;
import com.ssctech.vendingmachine.state.VendingMachineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vending Machine Integration Tests")
class VendingMachineTest {

    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        vendingMachine = new VendingMachine();
    }

    @Test
    @DisplayName("Should insert coin successfully")
    void shouldInsertCoinSuccessfully() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        VendingMachineState currentState = vendingMachine.getCurrentState();

        assertEquals(TestDataBuilder.money("1.00"), currentState.getTransaction().insertedAmount());
        assertEquals(1, currentState.getTransaction().insertedCoinTenders().size());
    }

    @Test
    @DisplayName("Should accumulate multiple coins")
    void shouldAccumulateMultipleCoins() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        vendingMachine.insertCoin(CoinTender.FIFTY_PENCE);
        VendingMachineState currentState = vendingMachine.getCurrentState();


        assertEquals(TestDataBuilder.money("1.50"), currentState.getTransaction().insertedAmount());
        assertEquals(2, currentState.getTransaction().insertedCoinTenders().size());
    }

    @Test
    @DisplayName("Should purchase product with exact amount")
    void shouldPurchaseProductWithExactAmount() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        vendingMachine.insertCoin(CoinTender.FIFTY_PENCE);

        PurchaseResult result = vendingMachine.selectProduct(Product.COKE);

        assertEquals(Product.COKE, result.product());
        assertEquals(Money.zero(), result.change());
        assertTrue(result.changeCoinTenders().isEmpty());
    }

    @Test
    @DisplayName("Should purchase product with change")
    void shouldPurchaseProductWithChange() {
        vendingMachine.insertCoin(CoinTender.TWO_POUND);

        PurchaseResult result = vendingMachine.selectProduct(Product.WATER);

        assertEquals(Product.WATER, result.product());
        assertEquals(TestDataBuilder.money("1.10"), result.change());
        assertFalse(result.changeCoinTenders().isEmpty());
    }

    @Test
    @DisplayName("Should fail purchase with insufficient funds")
    void shouldFailPurchaseWithInsufficientFunds() {
        vendingMachine.insertCoin(CoinTender.FIFTY_PENCE);

        VendingException exception = assertThrows(VendingException.class, () -> {
            vendingMachine.selectProduct(Product.COKE);
        });

        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    @DisplayName("Should fail purchase when product out of stock")
    void shouldFailPurchaseWhenProductOutOfStock() {
        // set product OOS
        for (int i = 0; i < 10; i++) {
            vendingMachine.insertCoin(CoinTender.TWO_POUND);
            vendingMachine.selectProduct(Product.COKE);
        }

        vendingMachine.insertCoin(CoinTender.TWO_POUND);
        VendingException exception = assertThrows(VendingException.class, () -> {
            vendingMachine.selectProduct(Product.COKE);
        });

        assertTrue(exception.getMessage().contains("Product out of stock"));
    }

    @Test
    @DisplayName("Should cancel transaction and return refund")
    void shouldCancelTransactionAndReturnRefund() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        vendingMachine.insertCoin(CoinTender.FIFTY_PENCE);

        RefundResult result = vendingMachine.cancelTransaction();

        assertEquals(TestDataBuilder.money("1.50"), result.refundAmount());
        assertEquals(2, result.refundCoinTenders().size());
    }

    @Test
    @DisplayName("Should return empty refund when no transaction")
    void shouldReturnEmptyRefundWhenNoTransaction() {
        RefundResult result = vendingMachine.cancelTransaction();

        assertEquals(Money.zero(), result.refundAmount());
        assertTrue(result.refundCoinTenders().isEmpty());
    }

    @Test
    @DisplayName("Should reset machine state")
    void shouldResetMachineState() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        vendingMachine.reset();
        VendingMachineState state = vendingMachine.getCurrentState();

        assertEquals(Money.zero(), state.getTransaction().insertedAmount());
        assertTrue(state.getTransaction().insertedCoinTenders().isEmpty());
    }

    @Test
    @DisplayName("Should clear transaction after successful purchase")
    void shouldClearTransactionAfterSuccessfulPurchase() {
        vendingMachine.insertCoin(CoinTender.ONE_POUND);
        vendingMachine.insertCoin(CoinTender.FIFTY_PENCE);
        vendingMachine.selectProduct(Product.COKE);

        VendingMachineState state = vendingMachine.getCurrentState();
        assertEquals(Money.zero(), state.getTransaction().insertedAmount());
        assertTrue(state.getTransaction().insertedCoinTenders().isEmpty());
    }
}