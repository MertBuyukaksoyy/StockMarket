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

    public List<Portfolio> getUserPortfolio(User user) {
        return portfolioRepo.findActiveByUser(user); // Only return active portfolios
    }

    public void createPortfolio(User user, Stock stock, int quantity) {
        Portfolio portfolio = new Portfolio(user, stock, quantity);
        portfolioRepo.save(portfolio);
    }

    @Transactional
    public void deletePortfolio(Portfolio portfolio) {
        portfolio.setDeleted(true);
        portfolioRepo.save(portfolio);
    }

    @Transactional
    public void updatePortfolio(User user, Stock stock, int quantity) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findByUserAndStockAndDeletedFalse(user, stock);

        Portfolio portfolio;
        if (existingPortfolio.isPresent()) {
            portfolio = existingPortfolio.get();
            int newQuantity = portfolio.getQuantity() + quantity;

            if (newQuantity == 0) {
                deletePortfolio(portfolio); // Soft delete
            } else {
                portfolio.setQuantity(newQuantity);
                portfolioRepo.save(portfolio);
            }
        } else {
            Optional<Portfolio> deletedPortfolio = portfolioRepo.findByUserAndStock(user, stock)
                    .filter(Portfolio::isDeleted);

            if (deletedPortfolio.isPresent()) {
                portfolio = deletedPortfolio.get();
                portfolio.setDeleted(false);
                portfolio.setQuantity(quantity);
                portfolioRepo.save(portfolio);
            } else if (quantity > 0) {
                portfolio = new Portfolio(user, stock, quantity);
                portfolioRepo.save(portfolio);
            }
        }
    }


}