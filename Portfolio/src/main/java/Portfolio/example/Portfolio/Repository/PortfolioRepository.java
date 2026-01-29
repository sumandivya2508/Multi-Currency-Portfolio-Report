package Portfolio.example.Portfolio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Portfolio.example.Portfolio.Entity.Portfolio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {
    @Query(
            value = "SELECT * FROM portfolio WHERE user_id = :userId",
            nativeQuery = true
    )
    List<Portfolio> findByUserId(@Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM portfolio " +
                    "WHERE id = :id " +
                    "AND user_id = :userId",
            nativeQuery = true
    )
    Optional<Portfolio> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

}
