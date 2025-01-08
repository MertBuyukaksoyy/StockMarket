package com.example.stockmarket.services;

import com.example.stockmarket.dao.TransactionRepo;
import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.Transactions;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.Stock;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private StockService stockService;

    private BigDecimal commissionRate = BigDecimal.valueOf(0.05);

    @Transactional
    public void buyStock(User user, Stock stock, int quantity) {
        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal totalCost = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        BigDecimal commission = totalCost.multiply(commissionRate);
        BigDecimal finalCost = totalCost.add(commission);

        if (!stock.getStockActive()) {
            throw new RuntimeException("Hisse Senedi Aktif Değil!");
        }

        if (balanceService.getBalance(user).getAmount().compareTo(finalCost) >= 0) {
            Transactions transaction = new Transactions(0, user, stock, finalCost, commission, true, pricePerUnit, quantity, LocalDateTime.now());
            transactionRepo.save(transaction);
            balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().subtract(finalCost));
            portfolioService.updatePortfolio(user, stock, quantity, totalCost);
            stockService.updateStockQuantity(stock, -quantity);
        } else {
            throw new RuntimeException("Insufficient balance\n");
        }
    }

    @Transactional
    public void sellStock(User user, Stock stock, int quantity) {
        Optional<Portfolio> optionalPortfolio = portfolioService.getUserPortfolio(user).stream()
                .filter(p -> p.getStock() != null && p.getStock().equals(stock) && p.getQuantity() >= quantity)
                .findFirst();

        if (!stock.getStockActive()) {
            throw new RuntimeException("Stock is not active!!!");
        }

        if (optionalPortfolio.isEmpty()) {
            throw new RuntimeException("Insufficient stock quantity or unknown stock");
        }

        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal totalProceeds = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        BigDecimal commission = totalProceeds.multiply(commissionRate);
        BigDecimal finalProceeds = totalProceeds.subtract(commission);
        Transactions transaction = new Transactions(0, user, stock, finalProceeds, commission, false, pricePerUnit, quantity, LocalDateTime.now());

        transactionRepo.save(transaction);
        balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().add(finalProceeds));
        portfolioService.updatePortfolio(user, stock, -quantity, BigDecimal.ZERO);
        stockService.updateStockQuantity(stock, quantity);
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal newRate) {
        this.commissionRate = newRate;
    }

    public List<Transactions> getUserTransactions(User user) {
        return transactionRepo.findByUser(user);
    }
}
