package com.example.stockmarket.dao;

import com.example.stockmarket.entity.BalanceCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BalanceCardRepo extends JpaRepository<BalanceCard, Integer> {
    Optional<BalanceCard> findByCardCode(String cardCode);
    List<BalanceCard> findByCreatedAtAfter(LocalDateTime createdAt);
}
