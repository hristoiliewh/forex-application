package com.forex.forex_application;

import com.forex.dto.ConversionRequestDTO;
import com.forex.dto.ConversionResponseDTO;
import com.forex.dto.ExchangeRateDTO;
import com.forex.exception.ExternalApiException;
import com.forex.exception.ValidationException;
import com.forex.model.CurrencyConversion;
import com.forex.model.ExternalRateApiResponse;
import com.forex.repository.CurrencyConversionRepository;
import com.forex.service.ForexServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ForexServiceImplTest {

    @Mock
    private CurrencyConversionRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ForexServiceImpl forexService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testGetExchangeRate_Success() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";

        ExternalRateApiResponse response = new ExternalRateApiResponse();
        response.setSuccess(true);
        response.setRates(Map.of("USD", 1.0, "EUR", 0.85));

        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);

        ExchangeRateDTO result = forexService.getExchangeRate(sourceCurrency, targetCurrency);

        assertNotNull(result);
        assertEquals("USD", result.getSourceCurrency());
        assertEquals("EUR", result.getTargetCurrency());
        assertEquals(0.85, result.getExchangeRate());
    }

    @Test
    public void testGetExchangeRate_FailedApiCall() {
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";

        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(null);

        assertThrows(ExternalApiException.class, () -> forexService.getExchangeRate(sourceCurrency, targetCurrency));
    }

    @Test
    public void testGetExchangeRate_InvalidCurrency() {
        String sourceCurrency = "USD";
        String targetCurrency = "INVALID";

        ExternalRateApiResponse response = new ExternalRateApiResponse();
        response.setSuccess(true);
        response.setRates(Map.of("USD", 1.0, "EUR", 0.85));

        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);

        assertThrows(ValidationException.class, () -> forexService.getExchangeRate(sourceCurrency, targetCurrency));
    }

    @Test
    public void testConvertCurrency_Success() {
        ConversionRequestDTO requestDTO = new ConversionRequestDTO(100, "USD", "EUR");

        ExternalRateApiResponse response = new ExternalRateApiResponse();
        response.setSuccess(true);
        response.setRates(Map.of("USD", 1.0, "EUR", 0.85));

        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);

        CurrencyConversion conversion = new CurrencyConversion(null, "USD", "EUR", 100, 85.0, "transactionId", LocalDate.now());
        when(repository.save(any(CurrencyConversion.class))).thenReturn(conversion);

        ConversionResponseDTO responseDTO = forexService.convertCurrency(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(85.0, responseDTO.getConvertedAmount());
        assertNotNull(responseDTO.getTransactionId());
        verify(repository, times(1)).save(any(CurrencyConversion.class));
    }

    @Test
    public void testConvertCurrency_InvalidCurrency() {
        ConversionRequestDTO requestDTO = new ConversionRequestDTO(100, "USD", "INVALID");

        ExternalRateApiResponse response = new ExternalRateApiResponse();
        response.setSuccess(true);
        response.setRates(Map.of("USD", 1.0, "GBP", 0.8));

        when(restTemplate.getForObject(anyString(), eq(ExternalRateApiResponse.class))).thenReturn(response);

        assertThrows(ValidationException.class, () -> forexService.convertCurrency(requestDTO));
        verify(repository, times(0)).save(any(CurrencyConversion.class));
    }

    @Test
    public void testGetConversionHistory_ByTransactionId() {
        String transactionId = "transactionId";
        CurrencyConversion conversion = new CurrencyConversion(null,"USD", "EUR", 100, 85.0, transactionId, LocalDate.now());

        when(repository.findByTransactionId(transactionId)).thenReturn(List.of(conversion));

        List<CurrencyConversion> history = forexService.getConversionHistory(transactionId, null, 0, 10);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(transactionId, history.get(0).getTransactionId());
    }

    @Test
    public void testGetConversionHistory_ByConversionDate() {
        LocalDate date = LocalDate.now();
        CurrencyConversion conversion = new CurrencyConversion(null, "USD", "EUR", 100, 85.0, "transactionId", date);

        when(repository.findByConversionDate(date)).thenReturn(List.of(conversion));

        List<CurrencyConversion> history = forexService.getConversionHistory(null, date, 0, 10);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(date, history.get(0).getConversionDate());
    }

    @Test
    public void testGetConversionHistory_InvalidRequest() {
        assertThrows(ValidationException.class, () -> forexService.getConversionHistory(null, null, 0, 10));
    }

}
