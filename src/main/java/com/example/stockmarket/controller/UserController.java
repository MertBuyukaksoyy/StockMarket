package com.example.stockmarket.controller;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.entity.*;
import com.example.stockmarket.security.CustomUserService;
import com.example.stockmarket.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        List<Role> roles = roleRepo.findAll();
        model.addAttribute("roles", roles);
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("role") String roleName,
                          Model model) {
        userService.addUser(username, password, roleName);
        return "redirect:/users";
    }
    @GetMapping("/users")
    public String showUserList(Model model){
        List<User> users = userService.getAllUsers();
        List<Balances> balances = users.stream()
                .map(user -> balanceService.getBalance(user))
                .toList();
        model.addAttribute("users", users);
        model.addAttribute("balances",balances);
        return "users";
    }
    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }


    @PostMapping("/authenticateUser")
    public String authenticate(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        boolean authenticated = userService.authenticateUser(username, password);

        if (authenticated) {
            UserDetails userDetails = customUserService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
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
}
