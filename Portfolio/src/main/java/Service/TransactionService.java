package Service;
import DTO_Request.TransactionRequest;
import DTO_Response.PositionResponse;
import DTO_Response.ReportResponse;
import DTO_Response.TransactionResponse;
import Entity.*;
import Exceptions.InsufficientSharesException;
import Repository.PositionRepository;
import Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
        Portfolio portfolio = portfolioService.findById(request.getPortfolioId(), username);

        BigDecimal exchangeRate = exchangeService.getExchangeRate(
                request.getCurrency(),
                request.getTransactionDate()
        );

        BigDecimal localAmount = request.getLocalPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal usdAmount = localAmount.multiply(exchangeRate)
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
                .status(TransactionStatus.PENDING)
                .build();

        try {
            if (transaction.getType() == TransactionType.BUY) {
                processBuy(portfolio, transaction);
            } else {
                processSell(portfolio, transaction);
            }

            transaction.setStatus(TransactionStatus.COMPLETED);
            log.info("{} transaction completed: {} shares of {} for ${}",
                    transaction.getType(), transaction.getQuantity(),
                    transaction.getTicker(), usdAmount);

        } catch (InsufficientSharesException e) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setErrorMessage(e.getMessage());
            log.error("Transaction rejected: {}", e.getMessage());
            throw e;
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    private void processBuy(Portfolio portfolio, Transaction transaction) {
        Position position = positionRepository
                .findByPortfolioIdAndTicker(portfolio.getId(), transaction.getTicker())
                .orElseGet(() -> Position.builder()
                        .portfolio(portfolio)
                        .ticker(transaction.getTicker())
                        .shares(0)
                        .totalUSDInvestment(BigDecimal.ZERO)
                        .build());

        position.setShares(position.getShares() + transaction.getQuantity());
        position.setTotalUSDInvestment(
                position.getTotalUSDInvestment().add(transaction.getUsdAmount())
        );
        position.updateAverageCost();

        positionRepository.save(position);
    }

    private void processSell(Portfolio portfolio, Transaction transaction) {
        Position position = positionRepository
                .findByPortfolioIdAndTicker(portfolio.getId(), transaction.getTicker())
                .orElseThrow(() -> new InsufficientSharesException(
                        "No position found for " + transaction.getTicker()));

        if (position.getShares() < transaction.getQuantity()) {
            throw new InsufficientSharesException(
                    String.format("Insufficient shares: tried to sell %d but only %d available for %s",
                            transaction.getQuantity(), position.getShares(), transaction.getTicker())
            );
        }

        position.setShares(position.getShares() - transaction.getQuantity());
        position.setTotalUSDInvestment(
                position.getTotalUSDInvestment().subtract(transaction.getUsdAmount())
        );
        position.updateAverageCost();

        positionRepository.save(position);
    }

    @Transactional(readOnly = true)
    public ReportResponse generateReport(String username, Long portfolioId) {
        Portfolio portfolio = portfolioService.findById(portfolioId, username);

        List<Position> activePositions = positionRepository
                .findByPortfolioIdAndSharesGreaterThan(portfolioId, 0);

        List<PositionResponse> positionResponses = activePositions.stream()
                .map(this::mapPositionToResponse)
                .collect(Collectors.toList());

        BigDecimal totalValue = activePositions.stream()
                .map(Position::getTotalUSDInvestment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Transaction> failedTxns = transactionRepository
                .findByPortfolioIdAndStatus(portfolioId, TransactionStatus.REJECTED);

        List<String> errors = failedTxns.stream()
                .map(Transaction::getErrorMessage)
                .collect(Collectors.toList());

        return ReportResponse.builder()
                .portfolioId(portfolio.getId())
                .portfolioName(portfolio.getName())
                .baseCurrency(portfolio.getBaseCurrency())
                .positions(positionResponses)
                .totalPortfolioValue(totalValue)
                .totalActivePositions(activePositions.size())
                .errors(errors)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(String username, Long portfolioId) {
        portfolioService.findById(portfolioId, username);

        return transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
