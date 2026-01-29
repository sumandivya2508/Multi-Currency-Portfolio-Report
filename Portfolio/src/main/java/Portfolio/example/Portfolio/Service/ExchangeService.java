package Portfolio.example.Portfolio.Service;

import Portfolio.example.Portfolio.Entity.Exchange;
import Portfolio.example.Portfolio.Exceptions.ExchangeRateNotFoundException;
import Portfolio.example.Portfolio.Repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Transactional
    public void saveExchangeRate(String currency, LocalDate date, BigDecimal rate) {
        Exchange exchangeRate = Exchange.builder()
                .currency(currency.toUpperCase())
                .rateDate(date)
                .rateToUSD(rate)
                .build();

        exchangeRateRepository.save(exchangeRate);
        log.info("Exchange rate saved: {} on {} = {}", currency, date, rate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getExchangeRate(String currency, LocalDate date) {
        if ("USD".equalsIgnoreCase(currency)) {
            return BigDecimal.ONE;
        }

        return exchangeRateRepository.findByCurrencyAndRateDate(currency.toUpperCase(), date)
                .map(Exchange::getRateToUSD)
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "Exchange rate not found for " + currency + " on " + date));
    }
}

