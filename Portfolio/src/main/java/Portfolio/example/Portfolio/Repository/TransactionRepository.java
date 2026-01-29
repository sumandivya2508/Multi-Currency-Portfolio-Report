package Portfolio.example.Portfolio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Portfolio.example.Portfolio.Entity.Transaction;
import Portfolio.example.Portfolio.Entity.TransactionStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(
            value = "SELECT * FROM transaction " +
                    "WHERE portfolio_id = :portfolioId " +
                    "ORDER BY transaction_date DESC",
            nativeQuery = true
    )
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(
            @Param("portfolioId") Long portfolioId
    );


    @Query(
            value = "SELECT * FROM transaction " +
                    "WHERE portfolio_id = :portfolioId " +
                    "AND status = :status",
            nativeQuery = true
    )
    List<Transaction> findByPortfolioIdAndStatus(
            @Param("portfolioId") Long portfolioId,
            @Param("status") TransactionStatus status
    );


    @Query(
            value = "SELECT * FROM transaction " +
                    "WHERE portfolio_id = :portfolioId " +
                    "AND transaction_date BETWEEN :startDate AND :endDate",
            nativeQuery = true
    )
    List<Transaction> findByPortfolioIdAndTransactionDateBetween(
            @Param("portfolioId") Long portfolioId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
