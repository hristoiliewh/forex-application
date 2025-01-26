package com.forex.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO representing the currency conversion response")
public class ConversionResponseDTO {

    @Schema(description = "Converted amount in target currency", example = "85.0")
    private double convertedAmount;

    @Schema(description = "Unique transaction ID for the conversion", example = "a1b2c3d4-e5f6-7g8h-9i10j11k12l13")
    private String transactionId;

    public ConversionResponseDTO(double convertedAmount, String transactionId) {
        this.convertedAmount = convertedAmount;
        this.transactionId = transactionId;
    }
}
