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


    @GetMapping("/create_user")
    public String createUser(@RequestParam String username, @RequestParam String password, @RequestParam String rolename) {
        userService.createUser(username, password, rolename);
        return "create_user";
    }
    @PostMapping("/admin/create")
    public String createUser(@ModelAttribute UserForm userForm, Model model) {
        userService.createUser(userForm.getUsername(), userForm.getPassword(), userForm.getRole());
        return "redirect:/create_user";
    }


    @GetMapping("/error")
    public String showLoginError(){
        return "login_error";
    }
}
