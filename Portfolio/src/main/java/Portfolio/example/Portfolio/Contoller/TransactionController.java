package Portfolio.example.Portfolio.Contoller;

import Portfolio.example.Portfolio.DTO_Request.TransactionRequest;
import Portfolio.example.Portfolio.DTO_Response.TransactionResponse;
import Portfolio.example.Portfolio.Service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.processTransaction(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable Long portfolioId,
            Authentication authentication) {

        List<TransactionResponse> history =
                transactionService.getTransactionHistory(
                        authentication.getName(),
                        portfolioId
                );

        return ResponseEntity.ok(history);
    }
}
