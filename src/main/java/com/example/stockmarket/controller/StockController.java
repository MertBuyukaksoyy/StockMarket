package com.example.stockmarket.controller;

import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.StockHistory;
import com.example.stockmarket.services.StockHistoryService;
import com.example.stockmarket.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class StockController {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockHistoryService stockHistoryService;

    @GetMapping("/history/{id}")
    public String getStockHistory(@PathVariable int id, Model model) {
        Stock stock = stockService.findById(id);
        if (stock == null) {
            return "error";
        }

        List<StockHistory> history = stockHistoryService.getStockHistoryById(id);
        model.addAttribute("stock", stock);
        model.addAttribute("history", history);
        return "stockHistory";
    }
}
