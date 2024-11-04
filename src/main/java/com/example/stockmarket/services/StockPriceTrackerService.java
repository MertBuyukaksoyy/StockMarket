package com.example.stockmarket.services;


import com.example.stockmarket.dao.StockAlertsRepo;
import com.example.stockmarket.dao.StockRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.entity.StockAlerts;
import com.example.stockmarket.entity.Transactions;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockPriceTrackerService {

    @Autowired
    private StockAlertsRepo stockAlertsRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private StockService stockService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome!");
        message.setText( username + ",\n\nWelcome to our Stock Market website!!\n\nWith Respects,\nStock Market Crew");

        javaMailSender.send(message);
    }

    public StockAlerts createAlert(User user,Stock stock, BigDecimal priceLimit){


        BigDecimal currentPrice = getCurrentStockPrice(stock.getStockSymbol());
        String alertType = priceLimit.compareTo(currentPrice) < 0 ? "below" : "above";
        StockAlerts alert = new StockAlerts(priceLimit, alertType, LocalDateTime.now());
        alert.setUser(user);
        alert.setStock(stock);
        return stockAlertsRepo.save(alert);

    }

    @Scheduled(fixedRate = 60000)
    public void checkAlert(){

        List<StockAlerts> alerts = stockAlertsRepo.findAll();
        for (StockAlerts alert : alerts){
            BigDecimal currentPrice = getCurrentStockPrice(alert.getStock().getStockSymbol());

            if ((alert.getAlertType().equals("above") && currentPrice.compareTo(alert.getPriceLimit())>0) ||
                    (alert.getAlertType().equals("below") && currentPrice.compareTo(alert.getPriceLimit())<0))
                sendEmail(alert.getUser().getEmail(), alert.getStock().getStockName(), currentPrice);
        }

    }

    public BigDecimal getCurrentStockPrice(String stockSymbol) {
        Stock stock = stockRepo.findByStockSymbol(stockSymbol);
        if (stock != null) {
            return stock.getCurrentPrice();
        }
        throw new RuntimeException("Stock cannot be found " + stockSymbol);
    }

    private void sendEmail(String to, String stockName, BigDecimal currentPrice){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Alarm for " + stockName);
        message.setText("Updated Price" + currentPrice);
        javaMailSender.send(message);

    }

    public List<StockAlerts> getUserAlerts(User user) {
        return  stockAlertsRepo.findByUser(user);
    }



}
