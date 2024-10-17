package com.example.stockmarket.entity;

import jakarta.persistence.*;
import org.hibernate.mapping.Join;

import java.math.BigDecimal;

@Entity
@Table(name = "balances")
public class Balances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private int balanceId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "amount")
    private BigDecimal amount;

    public Balances(){

    }
    public Balances(BigDecimal amount, User user) {
        this.amount = amount;
        this.user = user;
    }

    public int getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
