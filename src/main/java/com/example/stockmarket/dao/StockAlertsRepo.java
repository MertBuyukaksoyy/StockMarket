package com.example.stockmarket.dao;

import com.example.stockmarket.entity.StockAlerts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAlertsRepo extends JpaRepository<StockAlerts, Integer> {

    List<StockAlerts> findByStock_StockId(int stockId);

}
