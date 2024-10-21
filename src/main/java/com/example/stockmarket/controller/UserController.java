package com.example.stockmarket.controller;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/home")
    public String showHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
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
    @PostMapping("register")
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
        model.addAttribute("users", users);
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
            return "redirect:/home";
        } else {
            model.addAttribute("error", "İnvalid username or password");
            return "login";
        }
    }

    @GetMapping("/error")
    public String showLoginError(){
        return "error";
    }
}
