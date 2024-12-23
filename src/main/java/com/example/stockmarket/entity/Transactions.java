package com.example.stockmarket.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "price_per_unit")
    private BigDecimal pricePerUnit;
    @Column(name = "transaction_type")
    private Boolean transactionType;
    @Column(name = "comission")
    private BigDecimal comission;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    private Transactions(){

    }

    public Transactions(int transactionId, User user, Stock stock, BigDecimal amount, BigDecimal comission, Boolean transactionType, BigDecimal pricePerUnit, int quantity, LocalDateTime timeStamp) {
        this.transactionId = transactionId;
        this.user = user;
        this.stock = stock;
        this.amount = amount;
        this.comission = comission;
        this.transactionType = transactionType;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.timeStamp = timeStamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getComission() {
        return comission;
    }

    public void setComission(BigDecimal comission) {
        this.comission = comission;
    }

    public Boolean getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Boolean transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
