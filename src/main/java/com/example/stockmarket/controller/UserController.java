package com.example.stockmarket.controller;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.entity.*;
import com.example.stockmarket.security.CustomUserService;
import com.example.stockmarket.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private CustomUserService customUserService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private StockService stockService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/home")
    public String showHomePage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            User user = userService.findByUsername(userDetails.getUsername());
            Balances balance = balanceService.getBalance(user);
            //Portfolio portfolio = portfolioService.getUserPortfolio();
            model.addAttribute("balance", balance.getAmount());
        } else {
            model.addAttribute("username", "Anonymous");
        }
        List<Stock> stocks = stockService.getAllStocks();
        model.addAttribute("stocks", stocks);


        return "home";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showSignInForm(){
        return "register";
    }
    @PostMapping("/register")
    public String signInUser(@RequestParam ("username") String username
            , @RequestParam("password") String password, Model model){
        userService.register(username, password);
        return "redirect:/login";
    }

    @PostMapping("/authenticateUser")
    public String authenticate(@RequestParam("username") String username, @RequestParam("password") String password,
                               HttpServletRequest request, Model model) {
        boolean authenticated = userService.authenticateUser(username, password);

        if (authenticated) {
            UserDetails userDetails = customUserService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", authentication);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/error")
    public String showLoginError(){
        return "error";
    }

    @PostMapping("/buy/{id}")
    public String buyStock(@RequestParam int stockId, @RequestParam int quantity, @RequestParam String userName, Model model){
        User user = userService.findByUsername(userName);
        Stock stock = stockService.findById(stockId);
        String message;
        try {
            transactionService.buyStock(user, stock, quantity);
            message = "Hisse başarıyla satın alındı!";
        } catch (Exception e) {
            message = "Bir hata oluştu: " + e.getMessage();
        }

        model.addAttribute("message", message);
        return "purchaseResult";
    }
    @PostMapping("/sell/{id}")
    public String sellStock(@RequestParam int stockId, @RequestParam int quantity, @RequestParam String userName, Model model){
        User user = userService.findByUsername(userName);
        Stock stock = stockService.findById(stockId);
        String message;
        try {
            transactionService.sellStock(user, stock, quantity);
            message = "Hisse başarıyla satıldı!";
        } catch (Exception e) {
            message = "Bir hata oluştu: " + e.getMessage();
        }

        model.addAttribute("message", message);
        return "saleResult";
    }
    @GetMapping("/transactionHistory")
    public String showTransactionHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<Transactions> transactionsList = transactionService.getUserTransactions(user);
            model.addAttribute("transactionsList", transactionsList);
            model.addAttribute("username", username);
        } else {
            return "redirect:/error";
        }

        return "transactionHistory";
    }

    @GetMapping("/useBalanceCard")
    public String showUseBalanceCard(){
        return "useBalanceCard";
    }
    @PostMapping("/useBalanceCard")
    public String useBalanceCard(@RequestParam("cardCode") String cardCode,
                                 @RequestParam("userName") String userName,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            boolean usedSuccessfully = balanceService.useBalanceCard(user, cardCode);
            if (usedSuccessfully){
                model.addAttribute("message", "Kart Başarıyla Kullanıldı");
            }
            else {
                model.addAttribute("message", "Kart kullanım başarısız");
            }
        }
        return "redirect:/home";
    }
    @GetMapping("/downloadTransactionHistory")
    public void downloadTransactionHistory(HttpServletResponse response, Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());
        userService.generateExcel(response, user);
    }



}
