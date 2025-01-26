package com.forex.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ConversionRequestDTO {

    @NotNull(message = "Amount is required.")
    @Positive(message = "Value must be a positive number.")
    private double amount;
    @NotBlank(message = "Source currency is required.")
    private String sourceCurrency;
    @NotBlank(message = "Target currency is required.")
    private String targetCurrency;

    public ConversionRequestDTO(double amount, String sourceCurrency, String targetCurrency) {
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }
}
