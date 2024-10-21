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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserService userService;


    @GetMapping("/portfolio")
    private String showPortfolio(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        List<Portfolio> portfolioList = portfolioService.getUserPortfolio(currentUser);

        model.addAttribute("portfolioList", portfolioList);
        model.addAttribute("username", currentUsername);

        return "portfolio";
    }

}
