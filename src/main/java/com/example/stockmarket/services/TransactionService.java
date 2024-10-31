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
        BigDecimal totalCost = pricePerUnit.multiply(new BigDecimal(quantity));
        BigDecimal comission = totalCost.multiply(commissionRate);
        BigDecimal finalCost = totalCost.add(comission);


        if (balanceService.getBalance(user).getAmount().compareTo(finalCost) >= 0) {
            Transactions transaction = new Transactions(0, user, stock, finalCost, comission, true, pricePerUnit, quantity);
            transactionRepo.save(transaction);
            balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().subtract(finalCost));
            portfolioService.updatePortfolio(user, stock, quantity);
            stockService.updateStockQuantity(stock, -quantity);
        } else {
            throw new RuntimeException("Yetersiz bakiye.");
        }
    }

    @Transactional
    public void sellStock(User user, Stock stock, int quantity) {
        Optional<Portfolio> optionalPortfolio = portfolioService.getUserPortfolio(user).stream()
                .filter(p -> p.getStock() != null && p.getStock().equals(stock) && p.getQuantity() >= quantity)
                .findFirst();

        if (optionalPortfolio.isEmpty()) {
            throw new RuntimeException("Yetersiz hisse miktarı veya geçersiz hisse.");
        }

        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal totalProceeds = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        BigDecimal comission = totalProceeds.multiply(commissionRate);
        BigDecimal finalProceeds = totalProceeds.subtract(comission);
        Transactions transaction = new Transactions(0, user, stock, finalProceeds, comission, false, pricePerUnit, quantity);

        transactionRepo.save(transaction);
        balanceService.updateBalance(user, balanceService.getBalance(user).getAmount().add(finalProceeds));
        portfolioService.updatePortfolio(user, stock, -quantity);
        stockService.updateStockQuantity(stock, quantity);
    }
    public BigDecimal getComissionRate(){
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal newRate) {
        this.commissionRate = newRate;
    }


}
