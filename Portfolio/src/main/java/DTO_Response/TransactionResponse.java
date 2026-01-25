package DTO_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private LocalDate transactionDate;
    private String ticker;
    private String type;
    private Integer quantity;
    private BigDecimal localPrice;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal usdAmount;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;
}