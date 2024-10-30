package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepo extends JpaRepository<Portfolio, Integer> {
    List<Portfolio> findByUser(User user);
    Optional<Portfolio> findByUserAndStock(User user, Stock stock);

}
