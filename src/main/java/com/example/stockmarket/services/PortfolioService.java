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
        return portfolioRepo.findByUser(user);
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
        Portfolio portfolio = portfolioRepo.findByUserAndStock(user, stock)
                .filter(p -> !p.isDeleted())
                .orElse(new Portfolio(user, stock, 0));

        int newQuantity = portfolio.getQuantity() + quantity;
        portfolio.setQuantity(newQuantity);

        if (newQuantity <= 0) {
            deletePortfolio(portfolio); // Soft delete
        } else {
            portfolioRepo.save(portfolio);
        }
    }
}
