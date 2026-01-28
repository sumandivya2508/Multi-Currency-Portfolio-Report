package Repository;

import Entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.time.LocalDate;

public interface ExchangeRateRepository extends JpaRepository<Exchange,Long> {
    Optional<Exchange> findByCurrencyAndRateDate(String currency, LocalDate rateDate);
}
