package Portfolio.example.Portfolio.Service;

import Portfolio.example.Portfolio.DTO_Request.TransactionRequest;
import Portfolio.example.Portfolio.DTO_Response.PositionResponse;
import Portfolio.example.Portfolio.DTO_Response.ReportResponse;
import Portfolio.example.Portfolio.DTO_Response.TransactionResponse;
import Portfolio.example.Portfolio.Entity.*;
import Portfolio.example.Portfolio.Exceptions.InsufficientSharesException;
import Portfolio.example.Portfolio.Repository.PositionRepository;
import Portfolio.example.Portfolio.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    private final ExchangeService exchangeService;
    private final PortfolioService portfolioService;

    @Transactional
    public TransactionResponse processTransaction(String username, TransactionRequest request) {

        Portfolio portfolio = portfolioService.findById(
                request.getPortfolioId(), username
        );

        BigDecimal exchangeRate = exchangeService.getExchangeRate(
                request.getCurrency(),
                request.getTransactionDate()
        );

        BigDecimal localAmount = request.getLocalPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        BigDecimal usdAmount = localAmount
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        Transaction transaction = Transaction.builder()
                .portfolio(portfolio)
                .transactionDate(request.getTransactionDate())
                .ticker(request.getTicker().toUpperCase())
                .type(TransactionType.valueOf(request.getType()))
                .quantity(request.getQuantity())
                .localPrice(request.getLocalPrice())
                .currency(request.getCurrency().toUpperCase())
                .exchangeRate(exchangeRate)
                .usdAmount(usdAmount)
                .status(TransactionStatus.ROLLED_BACK)
                .build();

        try {
            if (transaction.getType() == TransactionType.BUY) {
                processBuy(portfolio, transaction);
            } else {
                processSell(portfolio, transaction);
            }

            transaction.setStatus(TransactionStatus.COMMITTED);

        } catch (InsufficientSharesException e) {
            transaction.setErrorMessage(e.getMessage());
            log.error("Transaction failed: {}", e.getMessage());
            throw e;
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    private void processBuy(Portfolio portfolio, Transaction transaction) {

        Position position = positionRepository
                .findByPortfolioIdAndTicker(
                        portfolio.getId(),
                        transaction.getTicker()
                )
                .orElseGet(() -> Position.builder()
                        .portfolio(portfolio)
                        .ticker(transaction.getTicker())
                        .shares(0)
                        .totalUSDInvestment(BigDecimal.ZERO)
                        .build()
                );

        position.setShares(position.getShares() + transaction.getQuantity());
        position.setTotalUSDInvestment(
                position.getTotalUSDInvestment().add(transaction.getUsdAmount())
        );
        position.updateAverageCost();

        positionRepository.save(position);
    }

    private void processSell(Portfolio portfolio, Transaction transaction) {

        Position position = positionRepository
                .findByPortfolioIdAndTicker(
                        portfolio.getId(),
                        transaction.getTicker()
                )
                .orElseThrow(() ->
                        new InsufficientSharesException("No position found")
                );

        if (position.getShares() < transaction.getQuantity()) {
            throw new InsufficientSharesException("Insufficient shares");
        }

        BigDecimal sellCost = position.getAverageCostPerShare()
                .multiply(BigDecimal.valueOf(transaction.getQuantity()));

        position.setShares(position.getShares() - transaction.getQuantity());
        position.setTotalUSDInvestment(
                position.getTotalUSDInvestment().subtract(sellCost)
        );
        position.updateAverageCost();

        positionRepository.save(position);
    }

    @Transactional(readOnly = true)
    public ReportResponse generateReport(String username, Long portfolioId) {

        Portfolio portfolio = portfolioService.findById(portfolioId, username);

        List<Position> activePositions =
                positionRepository.findByPortfolioIdAndSharesGreaterThan(
                        portfolioId, 0
                );

        List<PositionResponse> positionResponses =
                activePositions.stream()
                        .map(this::mapPositionToResponse)
                        .collect(Collectors.toList());

        List<String> errors =
                transactionRepository
                        .findByPortfolioIdAndStatus(
                                portfolioId,
                                TransactionStatus.ROLLED_BACK
                        )
                        .stream()
                        .map(Transaction::getErrorMessage)
                        .collect(Collectors.toList());

        return ReportResponse.builder()
                .portfolioId(portfolio.getId())
                .portfolioName(portfolio.getName())
                .baseCurrency(portfolio.getBaseCurrency())
                .positions(positionResponses)
                .totalPortfolioValue(
                        activePositions.stream()
                                .map(Position::getTotalUSDInvestment)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .totalActivePositions(activePositions.size())
                .errors(errors)
                .build();
    }

    private TransactionResponse mapToResponse(Transaction txn) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .transactionDate(txn.getTransactionDate())
                .ticker(txn.getTicker())
                .type(txn.getType().name())
                .quantity(txn.getQuantity())
                .localPrice(txn.getLocalPrice())
                .currency(txn.getCurrency())
                .exchangeRate(txn.getExchangeRate())
                .usdAmount(txn.getUsdAmount())
                .status(txn.getStatus().name())
                .errorMessage(txn.getErrorMessage())
                .createdAt(txn.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(
            String username,
            Long portfolioId) {

        Portfolio portfolio =
                portfolioService.findById(portfolioId, username);

        return transactionRepository
                .findByPortfolioIdOrderByTransactionDateDesc(portfolio.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    private PositionResponse mapPositionToResponse(Position pos) {
        return PositionResponse.builder()
                .id(pos.getId())
                .ticker(pos.getTicker())
                .shares(pos.getShares())
                .totalUSDInvestment(pos.getTotalUSDInvestment())
                .averageCostPerShare(pos.getAverageCostPerShare())
                .updatedAt(pos.getUpdatedAt())
                .build();
    }
}
