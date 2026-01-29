package Portfolio.example.Portfolio.Contoller;


import Portfolio.example.Portfolio.DTO_Request.PortfolioRequest;
import Portfolio.example.Portfolio.DTO_Response.PortfolioResponse;
import Portfolio.example.Portfolio.Service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody PortfolioRequest request,
            Authentication authentication) {
        PortfolioResponse response = portfolioService.createPortfolio(
                authentication.getName(), request
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getMyPortfolios(Authentication authentication) {
        List<PortfolioResponse> portfolios = portfolioService.getUserPortfolios(
                authentication.getName()
        );
        return ResponseEntity.ok(portfolios);
    }
}
