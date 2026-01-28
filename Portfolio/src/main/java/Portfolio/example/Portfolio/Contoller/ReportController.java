package Contoller;


import DTO_Response.ReportResponse;
import Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionService transactionService;

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<ReportResponse> getPortfolioReport(
            @PathVariable Long portfolioId,
            Authentication authentication) {
        ReportResponse report = transactionService.generateReport(
                authentication.getName(), portfolioId
        );
        return ResponseEntity.ok(report);
    }
}