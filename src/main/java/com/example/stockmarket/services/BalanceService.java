package com.example.stockmarket.services;

import com.example.stockmarket.dao.BalanceCardRepo;
import com.example.stockmarket.dao.BalanceRepo;
import com.example.stockmarket.entity.BalanceCard;
import com.example.stockmarket.entity.Balances;
import com.example.stockmarket.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepo balanceRepo;
    @Autowired
    private BalanceCardRepo balanceCardRepo;

    public void createBalance(User user) {
        Balances balance = new Balances(BigDecimal.ZERO, user);
        balanceRepo.save(balance);
    }

    public Balances getBalance(User user) {
        return balanceRepo.findByUser(user).orElseThrow(() -> new RuntimeException("Bakiye bulunamadı"));
    }
    public void updateBalance(User user, BigDecimal newAmount){
        Balances balances = getBalance(user);
        balances.setAmount(newAmount);
        balanceRepo.save(balances);
    }

    public Boolean useBalanceCard(User user, String cardCode){
        Optional<BalanceCard> balanceCardOpt = balanceCardRepo.findByCardCode(cardCode);
        if (balanceCardOpt.isPresent() && !balanceCardOpt.get().getUsed()){
            BalanceCard balanceCard = balanceCardOpt.get();
            updateBalance(user,getBalance(user).getAmount().add(balanceCard.getAmount()));
            balanceCard.setUsed(true);
            balanceCardRepo.save(balanceCard);
            return true;
        }
        return false;
    }
}
