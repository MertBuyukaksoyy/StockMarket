package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.StockHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepo extends JpaRepository<StockHistory , Integer> {
    List<StockHistory> findByStock(Stock stock);
   
}
