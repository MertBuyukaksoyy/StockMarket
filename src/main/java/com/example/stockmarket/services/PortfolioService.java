package com.example.stockmarket.services;

import com.example.stockmarket.dao.PortfolioRepo;
import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepo portfolioRepo;
    public void createPortfolio(User user,Stock stock ,int quantity){

        Portfolio portfolio = new Portfolio(user,stock,quantity);
        portfolioRepo.save(portfolio);


    }

    public List<Portfolio> getUserPortfolio(User user) {
        return portfolioRepo.findByUser(user);
    }

}
