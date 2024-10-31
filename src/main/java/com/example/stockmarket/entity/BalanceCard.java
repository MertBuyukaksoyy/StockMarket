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
    private Boolean used;

    public BalanceCard(){

    }

    public BalanceCard(String cardCode, BigDecimal amount, Boolean used) {
        this.cardCode = cardCode;
        this.amount = amount;
        this.used = used;
    }

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
