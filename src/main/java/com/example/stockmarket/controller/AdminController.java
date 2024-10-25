package com.example.stockmarket.controller;

import com.example.stockmarket.entity.Balances;
import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.services.BalanceService;
import com.example.stockmarket.services.StockService;
import com.example.stockmarket.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class AdminController {

        @Autowired
        private BalanceService balanceService;
        @Autowired
        private UserService userService;
        @Autowired
        private StockService stockService;

        @GetMapping("/updateBalance/{userId}")
        public String showUpdateBalanceForm(@PathVariable int userId, Model model) {
            User user = userService.findById(userId);
            Balances balance = balanceService.getBalance(user);
            model.addAttribute("balance", balance);
            return "updateBalance";
        }

        @Transactional
        @PostMapping("/updateBalance")
        public String updateBalance(@RequestParam Integer userId, @RequestParam BigDecimal newAmount) {
            User user = userService.findById(userId);
            balanceService.updateBalance(user, newAmount);
            return "redirect:/users";
        }

        @GetMapping("/updateStockStatus/{stockId}")
        public String showUpdateStockStatusForm(@PathVariable int stockId, Model model) {
            Stock stock = stockService.findById(stockId);
            model.addAttribute("stock", stock);
            return "updateStockForm";
        }

        @PostMapping("/updateStockStatus")
        @Transactional
        public String updateStockStatus(@RequestParam Integer stockId, @RequestParam Boolean isActive, @RequestParam Integer quantity) {
            Stock stock = stockService.findById(stockId);
            stock.setStockActive(isActive);
            stock.setStockQuantity(quantity);
            stockService.save(stock);
            return "redirect:/home";
        }


}

