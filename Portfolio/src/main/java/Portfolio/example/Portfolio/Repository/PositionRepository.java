package Portfolio.example.Portfolio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Portfolio.example.Portfolio.Entity.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position,Long> {
    @Query(
            value = "SELECT * FROM position WHERE portfolio_id = :portfolioId",
            nativeQuery = true
    )
    List<Position> findByPortfolioId(@Param("portfolioId") Long portfolioId);

    @Query(
            value = "SELECT * FROM position " +
                    "WHERE portfolio_id = :portfolioId " +
                    "AND ticker = :ticker",
            nativeQuery = true
    )
    Optional<Position> findByPortfolioIdAndTicker(
            @Param("portfolioId") Long portfolioId,
            @Param("ticker") String ticker
    );

    @Query(
            value = "SELECT * FROM position " +
                    "WHERE portfolio_id = :portfolioId " +
                    "AND shares > :shares",
            nativeQuery = true
    )
    List<Position> findByPortfolioIdAndSharesGreaterThan(
            @Param("portfolioId") Long portfolioId,
            @Param("shares") Integer shares
    );

}
