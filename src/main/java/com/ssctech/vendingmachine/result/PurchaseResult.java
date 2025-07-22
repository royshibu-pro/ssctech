package com.ssctech.vendingmachine.result;


import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;
import com.ssctech.vendingmachine.model.Product;

import java.util.List;

public record PurchaseResult(Product product, Money change, List<CoinTender> changeCoinTenders) {}
