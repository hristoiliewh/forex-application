package com.forex.controller;

import com.forex.model.dto.ConversionRequestDTO;
import com.forex.model.dto.ConversionResponseDTO;
import com.forex.model.dto.ExchangeRateDTO;
import com.forex.model.CurrencyConversion;
import com.forex.service.ForexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Get exchange rate between two currencies", description = "Retrieve the exchange rate between source and target currencies.")
    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateDTO> getExchangeRate(@Parameter(description = "Source currency code (3-letter uppercase)")
                                                           @RequestParam
                                                           @NotBlank(message = "Source currency is required.")
                                                           @Pattern(regexp = "^[A-Z]{3}$", message = "Source currency must be a 3-letter uppercase code.")
                                                           String sourceCurrency,

                                                           @Parameter(description = "Target currency code (3-letter uppercase)")
                                                           @RequestParam
                                                           @NotBlank(message = "Target currency is required.")
                                                           @Pattern(regexp = "^[A-Z]{3}$", message = "Target currency must be a 3-letter uppercase code.")
                                                           String targetCurrency) {
        ExchangeRateDTO exchangeRate = forexService.getExchangeRate(sourceCurrency, targetCurrency);
        return ResponseEntity.ok(exchangeRate);
    }

    @Operation(summary = "Convert currency", description = "Convert an amount of source currency to target currency using the current exchange rate.")
    @PostMapping("/convert")
    public ResponseEntity<ConversionResponseDTO> convertCurrency(@Valid @RequestBody ConversionRequestDTO request) {
        ConversionResponseDTO response = forexService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get conversion history", description = "Retrieve the history of currency conversions based on transaction ID or conversion date.")
    @GetMapping("/history")
    public ResponseEntity<List<CurrencyConversion>> getConversionHistory(
            @Parameter(description = "Transaction ID to filter conversions")
            @RequestParam(required = false) String transactionId,

            @Parameter(description = "Conversion date to filter conversions")
            @RequestParam(required = false) LocalDate conversionDate,

            @Parameter(description = "Page number for pagination")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of results per page")
            @RequestParam(defaultValue = "10") int size) {
        List<CurrencyConversion> history = forexService.getConversionHistory(transactionId, conversionDate, page, size);
        return ResponseEntity.ok(history);
    }
}

