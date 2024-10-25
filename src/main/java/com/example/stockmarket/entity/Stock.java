package com.example.stockmarket.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;
    @Column(name = "stock_name")
    private String stockName;
    @Column(name = "stock_symbol", unique = true)
    private String stockSymbol;
    @Column(name = "current_price")
    private BigDecimal currentPrice;
    @Column(name = "stock_active")
    private Boolean stockActive;
    @Column(name = "stock_quantity")
    private int stockQuantity;
    public Stock(){

    }

    public Stock(int stockId, Boolean stockActive, String stockName, BigDecimal currentPrice, String stockSymbol, int stockQuantity) {
        this.stockId = stockId;
        this.stockActive = stockActive;
        this.stockName = stockName;
        this.currentPrice = currentPrice;
        this.stockSymbol = stockSymbol;
        this.stockQuantity = stockQuantity;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Boolean getStockActive() {
        return stockActive;
    }

    public void setStockActive(Boolean stockActive) {
        this.stockActive = stockActive;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
