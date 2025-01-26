package com.forex.forex_application;

import com.forex.config.RateLimiter;
import com.forex.model.dto.ExchangeRateDTO;
import com.forex.model.exception.ExternalApiException;
import com.forex.model.exception.ValidationException;
import com.forex.model.CurrencyConversion;
import com.forex.model.ExternalRateApiResponse;
import com.forex.model.repository.CurrencyConversionRepository;
import com.forex.service.ForexServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForexServiceImplTest {

    @InjectMocks
    private ForexServiceImpl forexService;

    @Mock
    private CurrencyConversionRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private ExternalRateApiResponse externalRateApiResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    void testGetExchangeRateWhenRateLimitedAndCachedRateIsAvailable() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        ExchangeRateDTO cachedRate = new ExchangeRateDTO(sourceCurrency, targetCurrency, 0.85);

        when(rateLimiter.isRateLimited()).thenReturn(true);
        when(forexService.getCachedExchangeRate(sourceCurrency, targetCurrency)).thenReturn(cachedRate);

        ExchangeRateDTO result = forexService.getExchangeRate(sourceCurrency, targetCurrency);

        assertEquals(cachedRate, result);
        verify(rateLimiter, times(1)).isRateLimited();
    }

    @Test
    void testGetExchangeRateWhenRateLimitedAndNoCachedRate() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        ExchangeRateDTO fallbackRate = new ExchangeRateDTO(sourceCurrency, targetCurrency, 0.0);

        when(rateLimiter.isRateLimited()).thenReturn(true);
        when(forexService.getCachedExchangeRate(sourceCurrency, targetCurrency)).thenReturn(null);

        ExchangeRateDTO result = forexService.getExchangeRate(sourceCurrency, targetCurrency);

        assertEquals(fallbackRate.getExchangeRate(), result.getExchangeRate());
    }

    @Test
    void testGetExchangeRateWhenExternalApiCallSucceeds() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("USD", 1.0);

        ExternalRateApiResponse response = mock(ExternalRateApiResponse.class);
        when(response.getRates()).thenReturn(rates);
        when(response.isSuccess()).thenReturn(true);

        String apiUrl = "https://any.apiUrl";
        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);

        ExchangeRateDTO result = forexService.getExchangeRate(sourceCurrency, targetCurrency);

        assertEquals(0.85, result.getExchangeRate());
    }

    @Test
    void testGetExchangeRateWhenExternalApiCallFailsAndNoCachedRate() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";

        ExternalRateApiResponse response = mock(ExternalRateApiResponse.class);
        when(response.isSuccess()).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);
        when(forexService.getCachedExchangeRate(sourceCurrency, targetCurrency)).thenReturn(null);

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            forexService.getExchangeRate(sourceCurrency, targetCurrency);
        });
        assertEquals("Rates are temporarily unavailable. Please try again later.", exception.getMessage());
    }

    @Test
    void testGetConversionHistoryWithTransactionId() {
        String transactionId = "transactionId";
        List<CurrencyConversion> expectedConversions = List.of(new CurrencyConversion());
        when(repository.findByTransactionId(transactionId)).thenReturn(expectedConversions);

        List<CurrencyConversion> result = forexService.getConversionHistory(transactionId, null, 1, 10);

        assertEquals(expectedConversions, result);
        verify(repository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    void testGetConversionHistoryWithConversionDate() {
        LocalDate conversionDate = LocalDate.now();
        List<CurrencyConversion> expectedConversions = List.of(new CurrencyConversion());
        when(repository.findByConversionDate(conversionDate)).thenReturn(expectedConversions);

        List<CurrencyConversion> result = forexService.getConversionHistory(null, conversionDate, 1, 10);

        assertEquals(expectedConversions, result);
        verify(repository, times(1)).findByConversionDate(conversionDate);
    }

    @Test
    void testGetConversionHistoryWithValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            forexService.getConversionHistory(null, null, 1, 10);
        });
        assertEquals("Either transaction id or conversion date must be provided.", exception.getMessage());
    }
}
