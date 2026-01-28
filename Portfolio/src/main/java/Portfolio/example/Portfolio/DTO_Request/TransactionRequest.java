package DTO_Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @NotBlank
    @Size(min = 1, max = 10)
    private String ticker;

    @NotBlank
    @Pattern(regexp = "BUY|SELL", message = "Type must be BUY or SELL")
    private String type;

    @Min(1)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal localPrice;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;
}
