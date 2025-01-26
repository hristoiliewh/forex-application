package com.forex.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO representing the exchange rate between two currencies")
public class ExchangeRateDTO {

    @Schema(description = "Source currency code (3-letter uppercase)", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code (3-letter uppercase)", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Exchange rate from source currency to target currency", example = "0.85")
    private double exchangeRate;

    public ExchangeRateDTO(String sourceCurrency, String targetCurrency, double exchangeRate) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }
}
