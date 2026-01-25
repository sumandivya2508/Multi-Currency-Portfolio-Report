package DTO_Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PositionResponse {
    private Long id;
    private String ticker;
    private Integer shares;
    private BigDecimal totalUSDInvestment;
    private BigDecimal averageCostPerShare;
    private LocalDateTime updatedAt;
}
