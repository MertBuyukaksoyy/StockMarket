package com.example.stockmarket.services;

import com.example.stockmarket.dao.StockHistoryRepo;
import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.StockHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockHistoryService {
    @Autowired
    private StockHistoryRepo stockHistoryRepo;
    @Autowired
    private StockService stockService;

    public List<StockHistory> getStockHistoryById(int stockId){
        Stock stock = stockService.findById(stockId);
        return stockHistoryRepo.findByStock(stock);
    }
}
