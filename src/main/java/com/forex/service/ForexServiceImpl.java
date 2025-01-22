package com.forex.service;

import com.forex.dto.ConversionRequestDTO;
import com.forex.dto.ConversionResponseDTO;
import com.forex.dto.ExchangeRateDTO;
import com.forex.model.CurrencyConversion;
import com.forex.model.ExternalRateApiResponse;
import com.forex.repository.CurrencyConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ForexServiceImpl implements ForexService{

    @Autowired
    private CurrencyConversionRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    private final String fixerApiUrl = "http://data.fixer.io/api/";
    private final String accessKey = "71927bddc17178662ea3a661ba11fbeb";

    @Override
    public ExchangeRateDTO getExchangeRate(String sourceCurrency, String targetCurrency) {
        String url = String.format("%slatest?access_key=%s", fixerApiUrl, accessKey);
        ExternalRateApiResponse response = restTemplate.getForObject(url, ExternalRateApiResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch exchange rates.");
        }

        Map<String, Double> rates = response.getRates();
        if (!rates.containsKey(sourceCurrency) || !rates.containsKey(targetCurrency)) {
            throw new IllegalArgumentException("Invalid currency codes.");
        }

        double rate = rates.get(targetCurrency) / rates.get(sourceCurrency);
        return new ExchangeRateDTO(sourceCurrency, targetCurrency, rate);
    }

    @Override
    public ConversionResponseDTO convertCurrency(ConversionRequestDTO request) {
        ExchangeRateDTO rateDTO = getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
        double convertedAmount = request.getAmount() * rateDTO.getExchangeRate();

        String transactionId = UUID.randomUUID().toString();

        CurrencyConversion conversion = new CurrencyConversion(
                null,
                request.getSourceCurrency(),
                request.getTargetCurrency(),
                request.getAmount(),
                convertedAmount,
                transactionId,
                LocalDate.now()
        );
        repository.save(conversion);

        return new ConversionResponseDTO(convertedAmount, transactionId);
    }

    @Override
    public List<CurrencyConversion> getConversionHistory(String transactionId, LocalDate conversionDate, int page, int size) {
        if (transactionId != null) {
            return repository.findByTransactionId(transactionId);
        } else if (conversionDate != null) {
            return repository.findByConversionDate(conversionDate);
        } else {
            throw new IllegalArgumentException("Either transaction id or conversion date must be provided.");
        }
    }
}
