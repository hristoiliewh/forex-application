package com.forex.controller;

import com.forex.dto.ConversionRequestDTO;
import com.forex.dto.ConversionResponseDTO;
import com.forex.dto.ExchangeRateDTO;
import com.forex.model.CurrencyConversion;
import com.forex.service.ForexService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/forex")
public class ForexController {

    @Autowired
    private ForexService forexService;


    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateDTO> getExchangeRate(@RequestParam
                                                           @NotBlank(message = "Source currency is required.")
                                                           @Pattern(regexp = "^[A-Z]{3}$", message = "Source currency must be a 3-letter uppercase code.")
                                                           String sourceCurrency,

                                                           @RequestParam
                                                           @NotBlank(message = "Target currency is required.")
                                                           @Pattern(regexp = "^[A-Z]{3}$", message = "Target currency must be a 3-letter uppercase code.")
                                                           String targetCurrency) {
        ExchangeRateDTO exchangeRate = forexService.getExchangeRate(sourceCurrency, targetCurrency);
        return ResponseEntity.ok(exchangeRate);
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponseDTO> convertCurrency(@Valid @RequestBody ConversionRequestDTO request) {
        ConversionResponseDTO response = forexService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CurrencyConversion>> getConversionHistory(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) LocalDate conversionDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CurrencyConversion> history = forexService.getConversionHistory(transactionId, conversionDate, page, size);
        return ResponseEntity.ok(history);
    }
}
