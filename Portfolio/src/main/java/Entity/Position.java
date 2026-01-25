package Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "ticker"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false)
    @Builder.Default
    private Integer shares = 0;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalUSDInvestment = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal averageCostPerShare;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateAverageCost() {
        if (shares > 0) {
            this.averageCostPerShare = totalUSDInvestment
                    .divide(BigDecimal.valueOf(shares), 4, RoundingMode.HALF_UP);
        } else {
            this.averageCostPerShare = BigDecimal.ZERO;
        }
    }
}
