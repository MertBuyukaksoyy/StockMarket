package com.example.stockmarket.dao;

import com.example.stockmarket.entity.StockAlerts;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAlertsRepo extends JpaRepository<StockAlerts, Integer> {

    List<StockAlerts> findByUser(User user);

}
