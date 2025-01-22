package com.forex.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ConversionResponseDTO {

    private double convertedAmount;
    private String transactionId;

    public ConversionResponseDTO(double convertedAmount, String transactionId) {
        this.convertedAmount = convertedAmount;
        this.transactionId = transactionId;
    }
}
