package com.example.stockmarket.services;

import com.example.stockmarket.dao.TransactionRepo;
import com.example.stockmarket.entity.Transactions;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.Stock;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

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

    @Transactional
    public void buyStock(User user, Stock stock, int quantity) {
        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal comission = BigDecimal.ZERO;
        BigDecimal totalCost = pricePerUnit.multiply(new BigDecimal(quantity)).add(comission);

        if (balanceService.getBalance(user).getAmount().compareTo(totalCost) >= 0) {
            Transactions transaction = new Transactions(0, user, stock, totalCost, comission, true, pricePerUnit, quantity);
            transactionRepo.save(transaction);
            balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().subtract(totalCost));
            portfolioService.updatePortfolio(user, stock, quantity);
            stockService.updateStockQuantity(stock, -quantity);
        } else {
            throw new RuntimeException("Yetersiz bakiye.");
        }
    }

    @Transactional
    public void sellStock(User user, Stock stock, int quantity) {
        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal comission = BigDecimal.ZERO;
        BigDecimal totalProceeds = pricePerUnit.multiply(new BigDecimal(quantity)).subtract(comission);

        portfolioService.getUserPortfolio(user).stream()
                .filter(p -> p.getStock() != null && p.getStock().equals(stock) && p.getQuantity() >= quantity)
                .findFirst()
                .ifPresentOrElse(portfolio -> {
                    Transactions transaction = new Transactions(0, user, stock, totalProceeds, comission, false, pricePerUnit, quantity);
                    transactionRepo.save(transaction);
                    balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().add(totalProceeds));
                    portfolioService.updatePortfolio(user, stock, -quantity);
                    stockService.updateStockQuantity(stock, quantity);
                }, () -> {
                    throw new RuntimeException("Yetersiz hisse miktarı veya geçersiz hisse.");
                });

    }
}
