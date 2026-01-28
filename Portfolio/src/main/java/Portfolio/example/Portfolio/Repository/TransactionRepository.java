package Repository;

import Entity.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);
    List<Transaction> findByPortfolioIdAndStatus(Long portfolioId, TransactionStatus status);
    List<Transaction> findByPortfolioIdAndTransactionDateBetween(
            Long portfolioId, LocalDate startDate, LocalDate endDate);
}
