package com.example.stockmarket.services;

import com.example.stockmarket.dao.BalanceCardRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.entity.BalanceCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private BalanceCardRepo balanceCardRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepo userRepo;


    public void deleteUser(int id){
        userRepo.deleteById(id);
    }

    public void addBalanceCard(String cardCode, BigDecimal amount){

        BalanceCard balanceCard = new BalanceCard(cardCode, amount, false, LocalDateTime.now());
        balanceCard.setCreatedAt(LocalDateTime.now());
        balanceCardRepo.save(balanceCard);
        System.out.println("Added BalanceCard"+ cardCode);
        messagingTemplate.convertAndSend("/topic/notifications","New Balance Card Has Been Added !" + cardCode);
        System.out.println("Notification Sent");
    }

    public List<BalanceCard> getAllBalanceCards(){
        return balanceCardRepo.findAll();
    }
    public List<BalanceCard> getNewBalanceCardsSince(LocalDateTime lastLoginTime){
        return balanceCardRepo.findByCreatedAtAfter(lastLoginTime);
    }

}
