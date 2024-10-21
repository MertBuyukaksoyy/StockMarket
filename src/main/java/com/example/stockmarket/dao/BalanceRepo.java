package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Balances;
import com.example.stockmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceRepo extends JpaRepository<Balances, Integer> {
    Optional<Balances> findByUser(User user);
}
