package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Stock;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface StockRepo extends JpaRepository <Stock, Integer> {
    Stock findByStockSymbol(String stockSymbol);
    @Modifying
    @Transactional
    @Query("UPDATE Stock s SET s.currentPrice = :price, s.stockName = :name WHERE s.stockSymbol = :symbol")
    void bulkUpdateStockPrice(String symbol, BigDecimal price, String name);
}
