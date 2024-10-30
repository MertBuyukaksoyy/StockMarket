package com.example.stockmarket.services;

import com.example.stockmarket.dao.PortfolioRepo;
import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.Stock;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepo portfolioRepo;

    public Portfolio findOrCreatePortfolio(User user, Stock stock, int quantity) {
        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock).orElse(null);
        if (portfolio == null) {
            portfolio = new Portfolio(user, stock, quantity);
        } else {
            portfolio.setQuantity(portfolio.getQuantity() + quantity);
        }
        return portfolioRepo.save(portfolio);
    }

    public List<Portfolio> getUserPortfolio(User user) {
        return portfolioRepo.findByUser(user);
    }
    public void createPortfolio(User user, Stock stock, int quantity) {
        Portfolio portfolio = new Portfolio(user, stock, quantity);
        portfolioRepo.save(portfolio);
    }

    @Transactional
    public void updatePortfolio(User user, Stock stock, int quantity) {
        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock)
                .orElse(new Portfolio(user, stock, 0));
        portfolio.setQuantity(portfolio.getQuantity() + quantity);
        portfolioRepo.save(portfolio);
    }


}
