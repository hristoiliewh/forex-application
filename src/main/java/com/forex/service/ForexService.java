package com.forex.service;

import com.forex.model.dto.ConversionRequestDTO;
import com.forex.model.dto.ConversionResponseDTO;
import com.forex.model.dto.ExchangeRateDTO;
import com.forex.model.CurrencyConversion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ForexService {
    ExchangeRateDTO getExchangeRate(String sourceCurrency, String targetCurrency);
    ConversionResponseDTO convertCurrency(ConversionRequestDTO request);
    List<CurrencyConversion> getConversionHistory(String transactionId, LocalDate conversionDate, int page, int size);
}
