package com.example.stockmarket.controller;

import com.example.stockmarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/addUser")
    public String createUser(@ModelAttribute UserForm userForm, Model model) {
        userService.createUser(userForm.getUsername(), userForm.getPassword(), userForm.getRole());
        return "redirect:/home";
    }

    @PostMapping("/authenticateUser")
    public String authenticate(@RequestParam("username") String username, @RequestParam("password") String password) {
        boolean authenticated = userService.authenticateUser(username, password);

        if (authenticated) {
            return "redirect:/home";
        } else {
            return "error";
        }
    }
    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // Ana sayfa
    }

    @GetMapping("/error")
    public String showLoginError(){
        return "error";
    }
}
