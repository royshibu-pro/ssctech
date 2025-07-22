package com.ssctech.vendingmachine.result;


import com.ssctech.vendingmachine.model.CoinTender;
import com.ssctech.vendingmachine.model.Money;

import java.util.List;

public record RefundResult(Money refundAmount, List<CoinTender> refundCoinTenders) {}
