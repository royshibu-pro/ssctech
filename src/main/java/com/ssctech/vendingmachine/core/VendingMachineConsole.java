package com.ssctech.vendingmachine.core;

import com.ssctech.vendingmachine.exception.VendingException;
import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.InventoryItem;
import com.ssctech.vendingmachine.model.Money;
import com.ssctech.vendingmachine.model.Product;
import com.ssctech.vendingmachine.result.PurchaseResult;
import com.ssctech.vendingmachine.result.RefundResult;

import java.util.Map;
import java.util.Scanner;

public class VendingMachineConsole {
    private final VendingMachine vendingMachine;
    private final Scanner scanner;
    private final String supplierPin;

    public VendingMachineConsole() {
        this.vendingMachine = new VendingMachine();
        this.scanner = new Scanner(System.in);
        this.supplierPin = System.getenv("SUPPLIER_PIN") == null ? "0000" : System.getenv("SUPPLIER_PIN");
    }

    public void start() {
        System.out.println("==============================================");
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> insertCoinMenu();
                case "2" -> selectProductMenu();
                case "3" -> cancelTransaction();
                case "D" -> displayInventory();
                case "P" -> printLogs();
                case "R" -> resetMachine();
                case "Q" -> {
                    cancelTransaction();
                    System.out.println("Thank you for using the vending machine!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Insert Coin");
        System.out.println("2. Select Product");
        System.out.println("3. Cancel Transaction");
        System.out.println("Q. Quit");
        System.out.println("**** admin only****");
        System.out.println("D. Display Inventory (admin)");
        System.out.println("P. Print Logs (admin)");
        System.out.println("R. Reset Machine (admin)");
        System.out.println("*******************");
        System.out.println("Amount available : " + vendingMachine.getCurrentState().getTransaction().insertedAmount());
        System.out.print("Choose an option: ");
    }

    private void insertCoinMenu() {
        System.out.println("\n=== INSERT COIN ===");
        System.out.println("Available coins:");
        for (int i = 0; i < CoinTender.values().length; i++) {
            CoinTender coinTender = CoinTender.values()[i];
            System.out.printf("%d. %s (%s)%n", i + 1, coinTender, Money.of(coinTender.getValue()));
        }
        System.out.print("Select coin (1-6): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= CoinTender.values().length) {
                vendingMachine.insertCoin(CoinTender.values()[choice - 1]);
            } else {
                System.out.println("Invalid coin selection");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void selectProductMenu() {
        System.out.println("\n=== SELECT PRODUCT ===");
        for (int i = 0; i < Product.values().length; i++) {
            Product product = Product.values()[i];
            System.out.printf("%d. %s - %s%n", i + 1, product.getName(), Money.of(product.getPrice()));
        }
        System.out.print("Select product (1-3): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= Product.values().length) {
                Product selectedProduct = Product.values()[choice - 1];
                PurchaseResult purchaseResult = vendingMachine.selectProduct(selectedProduct);
                System.out.printf("Product dispensed: %s%n", purchaseResult.product().getName());
                if (purchaseResult.change().isGreaterThan(Money.zero())) {
                    System.out.printf("Change: %s %s%n", purchaseResult.change(), purchaseResult.changeCoinTenders());
                } else {
                    System.out.println("Invalid product selection");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (VendingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelTransaction() {
        RefundResult result = vendingMachine.cancelTransaction();
        if (result.refundAmount().isGreaterThan(Money.zero())) {
            System.out.printf("Refund dispensed: %s%n", result.refundAmount());
        }
    }

    private void displayInventory() {
        if (verifyPin()) {
            Map<Product, InventoryItem> items = vendingMachine.getCurrentState().getInventory().getItems();
            System.out.println("\n=== INVENTORY STATUS ===");
            items.values().forEach(item ->
                    System.out.printf("%s: %d units remaining (Price: %s)%n",
                            item.product().getName(),
                            item.stock(),
                            Money.of(item.product().getPrice())));
            System.out.println("========================");
            System.out.println("Coins available in the machine");
            Map<CoinTender, Integer> coins = vendingMachine.getCurrentState().getCoinBox().getCoins();
            coins.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        CoinTender coin = entry.getKey();
                        Integer count = entry.getValue();
                        System.out.printf("%s(%s) x %d%n", coin, Money.of(coin.getValue()), count);
                    });
            System.out.println("========================");
            System.out.println("Total amount " + vendingMachine.getCurrentState().getCoinBox().getTotalValue());
            System.out.println("========================");

        } else {
            System.out.println("Invalid PIN. Access denied.");
        }
    }

    private void resetMachine() {
        if (verifyPin()) {
            System.out.print("Are you sure you want to reset the machine? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(confirmation) || "yes".equals(confirmation)) {
                vendingMachine.reset();
            } else {
                System.out.println("Reset cancelled.");
            }
        } else {
            System.out.println("Invalid PIN. Reset denied.");
        }
    }

    private boolean verifyPin() {
        System.out.print("Enter supplier PIN: ");
        String enteredPin = scanner.nextLine().trim();
        return supplierPin.equals(enteredPin);
    }

    private void printLogs() {
        if (verifyPin()) {
            vendingMachine.getAuditLog().print();
        } else {
            System.out.println("Invalid PIN. Request denied.");
        }
    }
}