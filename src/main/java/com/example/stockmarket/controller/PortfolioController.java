package com.example.stockmarket.controller;

import com.example.stockmarket.entity.Portfolio;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.services.PortfolioService;
import com.example.stockmarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserController userController;


    @GetMapping("/portfolio")
    private String showPortfolio(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<Portfolio> portfolioList = portfolioService.getUserPortfolio(user)
                    .stream().filter(portfolio -> !portfolio.isDeleted()).collect(Collectors.toList());

            portfolioList.forEach(portfolio -> {
                if (portfolio.getStock() != null && portfolio.getCost() != null) {
                    BigDecimal currentPrice = portfolio.getStock().getCurrentPrice();
                    portfolio.setProfit(portfolioService.calculateProfit(currentPrice, portfolio.getCost(), portfolio.getQuantity()));
                }
                else {
                    portfolio.setProfit(BigDecimal.ZERO);
                }
            });

            model.addAttribute("portfolioList", portfolioList);
            model.addAttribute("username", username);
        } else {
            return "redirect:/error";
        }

        return "portfolio";
    }

}

