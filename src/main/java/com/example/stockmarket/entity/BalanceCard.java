package com.example.stockmarket.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "balance_card")
public class BalanceCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private int codeId;
    @Column(name = "card_code")
    private String cardCode;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "used")
    private byte used;
}
