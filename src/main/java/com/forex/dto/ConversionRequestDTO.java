package com.forex.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ConversionRequestDTO {

    @NotNull
    private Double amount;
    @NotNull
    private String sourceCurrency;
    @NotNull
    private String targetCurrency;
}
