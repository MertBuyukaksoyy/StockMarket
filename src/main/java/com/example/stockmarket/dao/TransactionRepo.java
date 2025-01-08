package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.Transactions;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepo extends JpaRepository <Transactions, Integer> {
    List<Transactions> findByUser(User user);
    List<Transactions> findByUserAndStockAndTransactionType(User user, Stock stock, Boolean isBuy);


}
