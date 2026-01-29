package Portfolio.example.Portfolio.DTO_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReportResponse {
    private Long portfolioId;
    private String portfolioName;
    private String baseCurrency;
    private List<PositionResponse> positions;
    private BigDecimal totalPortfolioValue;
    private Integer totalActivePositions;
    private List<String> errors;
}
