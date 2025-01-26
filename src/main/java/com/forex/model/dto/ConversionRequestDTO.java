package com.forex.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO representing the currency conversion request")
public class ConversionRequestDTO {

    @NotNull(message = "Amount is required.")
    @Positive(message = "Value must be a positive number.")
    @Schema(description = "Amount to be converted", example = "100.0")
    private double amount;

    @NotBlank(message = "Source currency is required.")
    @Schema(description = "Source currency code (3-letter uppercase)", example = "USD")
    private String sourceCurrency;

    @NotBlank(message = "Target currency is required.")
    @Schema(description = "Target currency code (3-letter uppercase)", example = "EUR")
    private String targetCurrency;

    public ConversionRequestDTO(double amount, String sourceCurrency, String targetCurrency) {
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }
}
