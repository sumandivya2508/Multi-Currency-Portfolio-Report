package Repository;

import Entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position,Long> {
    List<Position> findByPortfolioId(Long portfolioId);
    Optional<Position> findByPortfolioIdAndTicker(Long portfolioId, String ticker);
    List<Position> findByPortfolioIdAndSharesGreaterThan(Long portfolioId, Integer shares);
}
