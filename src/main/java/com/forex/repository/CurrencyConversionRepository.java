package com.forex.repository;

import com.forex.model.CurrencyConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {
    List<CurrencyConversion> findByTransactionId(String transactionId);
    List<CurrencyConversion> findByConversionDate(LocalDate conversionDate);
}
