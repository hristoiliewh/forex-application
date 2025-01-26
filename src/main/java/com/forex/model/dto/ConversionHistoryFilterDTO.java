package com.forex.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ConversionHistoryFilterDTO {

    private String transactionId;
    private LocalDate conversionDate;
}
