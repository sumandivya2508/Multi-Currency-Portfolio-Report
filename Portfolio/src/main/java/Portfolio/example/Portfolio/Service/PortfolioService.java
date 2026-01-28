package Service;

import DTO_Request.PortfolioRequest;
import DTO_Response.PortfolioResponse;
import Entity.Portfolio;
import Entity.User;
import Exceptions.ResourceNotFoundException;
import Repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    @Transactional
    public PortfolioResponse createPortfolio(String username, PortfolioRequest request) {
        User user = userService.findByUsername(username);

        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .baseCurrency(request.getBaseCurrency())
                .user(user)
                .build();

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Portfolio created: {} for user: {}", saved.getName(), username);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getUserPortfolios(String username) {
        User user = userService.findByUsername(username);
        return portfolioRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Portfolio findById(Long id, String username) {
        User user = userService.findByUsername(username);
        return portfolioRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + id));
    }

    private PortfolioResponse mapToResponse(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .baseCurrency(portfolio.getBaseCurrency())
                .totalPositions(portfolio.getPositions().size())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}