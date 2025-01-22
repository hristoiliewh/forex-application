package com.forex.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRateDTO {

    private String sourceCurrency;
    private String targetCurrency;
    private double exchangeRate;

    public ExchangeRateDTO(String sourceCurrency, String targetCurrency, double exchangeRate) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }
}
