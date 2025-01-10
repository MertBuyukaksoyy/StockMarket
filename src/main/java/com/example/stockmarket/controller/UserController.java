package com.example.stockmarket.controller;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.StockAlertsRepo;
import com.example.stockmarket.dao.StockRepo;
import com.example.stockmarket.entity.*;
import com.example.stockmarket.security.CustomUserService;
import com.example.stockmarket.security.JwtUtil;
import com.example.stockmarket.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
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
    @Autowired
    private StockPriceTrackerService stockPriceTrackerService;
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private StockAlertsRepo stockAlertsRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Show Home Page", description = "Returns home page with user details and stock list")
    @ApiResponse(responseCode = "200", description = "Successfully Displayed")
    @GetMapping("/home")
    public String showHomePage(HttpServletRequest request, Model model) {
        String token = extractJwtFromRequest(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Cookie Name: " + cookie.getName() + ", Value: " + cookie.getValue());
            }
        }

        System.out.println("Extracted Token: " + token);

        if (authentication != null && token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String username = jwtUtil.extractUsername(token);
            model.addAttribute("username", username);

            User user = userService.findByUsername(username);
            if (user != null) {
                Balances balance = balanceService.getBalance(user);
                model.addAttribute("balance", balance != null ? balance.getAmount() : 0.0);
            }
        } else {
            model.addAttribute("username", "Anonymous");
        }

        List<Stock> stocks = stockService.getAllStocks();
        model.addAttribute("stocks", stocks);
        return "home";
    }

    @Operation(summary = "Authenticate User", description = "Authenticate user and create JWT")
    @PostMapping("/authenticateUser")
    public ResponseEntity<Void> authenticateUser(@RequestBody AuthRequest authRequest,
                                   HttpServletResponse response) {
        String token = userService.authenticateUser(authRequest.getUsername(), authRequest.getPassword());
        if (token != null) {
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60*60*2);
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/home")).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Operation(summary = "Login Form", description = "Display Login Page")
    @ApiResponse(responseCode = "200", description = "Successfully displayed login form")
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showSignInForm(){
        return "register";
    }

    @Operation(summary = "Register User ", description = "Register a new user and send welcome email")
    @ApiResponse(responseCode = "302", description = "Redirects login page upon successful registration")
    @PostMapping("/register")
    public String signInUser(@RequestParam ("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("email") String email,
                             Model model){
        userService.register(username, password, email);
        stockPriceTrackerService.sendWelcomeEmail(email,username);
        return "redirect:/login";
    }

    @GetMapping("/error")
    public String showLoginError(){
        return "error";
    }

    @Operation(summary = "Buy stock", description = "Allow user to buy stocks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully buys stock"),
            @ApiResponse(responseCode = "400", description = "Failed to buy stock")
    })
    @SecurityRequirement(name = "Bearer Authentication")
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

    @Operation(summary = "Sell stock", description = "Allow user to sell stocks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sold stock"),
            @ApiResponse(responseCode = "400", description = "Failed to sell stock")
    })
    @SecurityRequirement(name = "Bearer Authentication")
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

    @Operation(summary = "Show Transaction History of User", description = "Display the transaction history of user that logged in ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully displayed transaction history."),
            @ApiResponse(responseCode = "302", description = "Redirects to error page if user is not authenticated.")
    })
    @SecurityRequirement(name = "Authorization")
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
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            boolean usedSuccessfully = balanceService.useBalanceCard(user, cardCode);
            if (usedSuccessfully){
                model.addAttribute("message", "Card Used Successfully");
            }
            else {
                model.addAttribute("message", "Card Usage Is not Successful");
            }
        }
        return "redirect:/home";
    }

    @GetMapping("/downloadTransactionHistory")
    public void downloadTransactionHistory(HttpServletResponse response, Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());
        userService.generateExcel(response, user);
    }

    @PostMapping("/createAlert")
    public String createAlert(@RequestParam String stockSymbol, @RequestParam BigDecimal priceLimit,
                              Principal principal) {
        String userName = principal.getName();
        User user = userService.findByUsername(userName);
        Stock stock = stockRepo.findByStockSymbol(stockSymbol);
        stockPriceTrackerService.createAlert(user, stock, priceLimit);

        return "redirect:/home";
    }

    @GetMapping("/showAlarms")
    public String showAlarms(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<StockAlerts> stockAlerts = stockPriceTrackerService.getUserAlerts(user);
            model.addAttribute("stockAlertsList", stockAlerts);
            model.addAttribute("username", username);

        } else {
            return "redirect:/error";
        }
        return "showAlarms";
    }

}
