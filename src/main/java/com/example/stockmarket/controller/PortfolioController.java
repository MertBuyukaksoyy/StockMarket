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
    @Autowired
    private UserController userController;


    @GetMapping("/portfolio")
    private String showPortfolio(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<Portfolio> portfolio = portfolioService.getUserPortfolio(user);
            model.addAttribute("portfolio", portfolio);
            model.addAttribute("username", username);
        } else {
            return "redirect:/error";
        }

        return "portfolio";
    }

}

