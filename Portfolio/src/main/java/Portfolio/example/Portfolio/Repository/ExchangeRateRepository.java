package Portfolio.example.Portfolio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Portfolio.example.Portfolio.Entity.Exchange;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<Exchange,Long> {
    @Query(
            value = "SELECT * FROM exchange " +
                    "WHERE currency = :currency " +
                    "AND rate_date = :rateDate",
            nativeQuery = true
    )
    Optional<Exchange> findByCurrencyAndRateDate(
            @Param("currency") String currency,
            @Param("rateDate") LocalDate rateDate
    );

}
