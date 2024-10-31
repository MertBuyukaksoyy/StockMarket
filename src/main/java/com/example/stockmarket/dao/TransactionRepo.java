package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transactions;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository <Transactions, Integer> {
    List<Transactions> findByUser(User user);
    @Query("SELECT t FROM Transactions t ORDER BY t.transactionId DESC")
    Optional<Transactions> findTopByOrderByTransaction();
}
