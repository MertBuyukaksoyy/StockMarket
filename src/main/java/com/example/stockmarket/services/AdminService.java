package com.example.stockmarket.services;

import com.example.stockmarket.dao.BalanceCardRepo;
import com.example.stockmarket.entity.BalanceCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private BalanceCardRepo balanceCardRepo;

    public void addBalanceCard(String cardCode, BigDecimal amount){

        BalanceCard balanceCard = new BalanceCard(cardCode, amount, false);
        balanceCardRepo.save(balanceCard);
    }

    public List<BalanceCard> getAllBalanceCards(){
        return balanceCardRepo.findAll();
    }

}
