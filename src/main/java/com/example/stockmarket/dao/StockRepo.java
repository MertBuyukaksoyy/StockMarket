package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepo extends JpaRepository <Stock, Integer> {
    Stock findByStockSymbol(String stockSymbol);
}
