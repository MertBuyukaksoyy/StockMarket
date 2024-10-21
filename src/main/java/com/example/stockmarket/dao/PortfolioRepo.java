package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepo extends JpaRepository<Portfolio, Integer> {
    List<Portfolio> findByUser(User user);

}
