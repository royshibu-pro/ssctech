package com.ssctech.vendingmachine.state;

import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.CoinBox;
import com.ssctech.vendingmachine.model.Inventory;
import com.ssctech.vendingmachine.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vending Machine State Tests")
class VendingMachineStateTest {
    
    @Test
    @DisplayName("Should create initial state")
    void shouldCreateInitializeState() {
        VendingMachineState state = VendingMachineState.initialize();
        
        assertNotNull(state.getInventory());
        assertNotNull(state.getCoinBox());
        assertNotNull(state.getTransaction());
        assertEquals(Money.zero(), state.getTransaction().insertedAmount());
    }
    
    @Test
    @DisplayName("Should create new state with updated inventory")
    void shouldCreateNewStateWithUpdatedInventory() {
        VendingMachineState state = VendingMachineState.initialize();
        Inventory newInventory = new Inventory();
        
        VendingMachineState newState = state.withInventory(newInventory);
        
        assertNotSame(state, newState);
        assertSame(newInventory, newState.getInventory());
        assertSame(state.getCoinBox(), newState.getCoinBox());
        assertSame(state.getTransaction(), newState.getTransaction());
    }
    
    @Test
    @DisplayName("Should create new state with updated coin bank")
    void shouldCreateNewStateWithUpdatedCoinBank() {
        VendingMachineState state = VendingMachineState.initialize();
        CoinBox newCoinBox = new CoinBox();
        
        VendingMachineState newState = state.withCoinBox(newCoinBox);
        
        assertNotSame(state, newState);
        assertSame(newCoinBox, newState.getCoinBox());
        assertSame(state.getInventory(), newState.getInventory());
        assertSame(state.getTransaction(), newState.getTransaction());
    }
    
    @Test
    @DisplayName("Should create new state with updated transaction")
    void shouldCreateNewStateWithUpdatedTransaction() {
        VendingMachineState state = VendingMachineState.initialize();
        TransactionState newTransaction = TransactionState.empty().addCoin(CoinTender.ONE_POUND);
        
        VendingMachineState newState = state.withTransaction(newTransaction);
        
        assertNotSame(state, newState);
        assertSame(newTransaction, newState.getTransaction());
        assertSame(state.getInventory(), newState.getInventory());
        assertSame(state.getCoinBox(), newState.getCoinBox());
    }
}