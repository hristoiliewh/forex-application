package com.forex.service;

import com.forex.config.RateLimiter;
import com.forex.model.dto.ConversionRequestDTO;
import com.forex.model.dto.ConversionResponseDTO;
import com.forex.model.dto.ExchangeRateDTO;
import com.forex.model.exception.ExternalApiException;
import com.forex.model.exception.ValidationException;
import com.forex.model.CurrencyConversion;
import com.forex.model.ExternalRateApiResponse;
import com.forex.model.repository.CurrencyConversionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ForexServiceImpl implements ForexService{

    @Autowired
    private CurrencyConversionRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RateLimiter rateLimiter;
    @Autowired
    private CacheManager cacheManager;

    @Value("${fixer.api.url}")
    private String fixerApiUrl;
    @Value("${fixer.api.access-key}")
    private String fixerApiAccessKey;

    @Override
    @Cacheable(value = "exchangeRates", key = "#sourceCurrency + '_' + #targetCurrency")
    public ExchangeRateDTO getExchangeRate(String sourceCurrency, String targetCurrency) {
        if (rateLimiter.isRateLimited()) {
            ExchangeRateDTO cachedRate = getCachedExchangeRate(sourceCurrency, targetCurrency);
            if (cachedRate != null) {
                log.info("RATE LIMITED - Returning cached rate for {} to {}", sourceCurrency, targetCurrency);
                return cachedRate;
            }
            log.warn("RATE LIMITED - No cached rate available. Returning fallback rate for {} to {}", sourceCurrency, targetCurrency);
            return new ExchangeRateDTO(sourceCurrency, targetCurrency, -1.0);
        }

        try {
            String url = fixerApiUrl + "latest?access_key=" + fixerApiAccessKey;
            ExternalRateApiResponse response = restTemplate.getForObject(url, ExternalRateApiResponse.class);

            if (response == null || !response.isSuccess()) {
                throw new ExternalApiException("Error retrieving exchange rates from external API.");
            }

            Map<String, Double> rates = response.getRates();
            double rate = rates.get(targetCurrency) / rates.get(sourceCurrency);
            return new ExchangeRateDTO(sourceCurrency, targetCurrency, rate);
        } catch (Exception e){
            log.error("EXTERNAL API ERROR: {}", e.getMessage(), e);
            ExchangeRateDTO cachedRate = getCachedExchangeRate(sourceCurrency, targetCurrency);
            if (cachedRate != null) {
                log.info("Returning cached rate for {} to {} after API error", sourceCurrency, targetCurrency);
                return cachedRate;
            }
            log.error("No cached rate available. Throwing exception for {} to {}", sourceCurrency, targetCurrency);
            throw new ExternalApiException("Rates are temporarily unavailable. Please try again later.");
        }
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
            throw new ValidationException("Either transaction id or conversion date must be provided.");
        }
    }

    public ExchangeRateDTO getCachedExchangeRate(String sourceCurrency, String targetCurrency) {
        return cacheManager.getCache("exchangeRates")
                .get(sourceCurrency + "_" + targetCurrency, ExchangeRateDTO.class);
    }

}
