package com.forex.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExternalRateApiResponse {

    private boolean success;
    private String base;
    private Map<String, Double> rates;
}
