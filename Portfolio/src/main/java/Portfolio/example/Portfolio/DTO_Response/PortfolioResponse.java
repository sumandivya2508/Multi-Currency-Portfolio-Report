package Portfolio.example.Portfolio.DTO_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PortfolioResponse {
    private Long id;
    private String name;
    private String description;
    private String baseCurrency;
    private Integer totalPositions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
