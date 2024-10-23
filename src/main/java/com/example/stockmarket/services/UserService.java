package com.example.stockmarket.services;

import com.example.stockmarket.dao.PortfolioRepo;
import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import jakarta.transaction.Transactional;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private BalanceService balanceService;

    public void register(String username, String password) {
        User user = new User(username, password);
        userRepo.save(user);
        portfolioService.createPortfolio(user,null,0);
        balanceService.createBalance(user);
        Optional<Role> userRoleOpt = roleRepo.findByRoleName("user");
        if (userRoleOpt.isPresent()) {
            UserRole userRole = new UserRole(user, userRoleOpt.get());
            userRoleRepo.save(userRole);
        } else {
            throw new RuntimeException("user rolü bulunamadı!");
        }
    }

    public void addUser(String username, String password, String roleName){
        User user = new User(username, password);
        userRepo.save(user);
        portfolioService.createPortfolio(user,null,0);
        balanceService.createBalance(user);
        Optional<Role> selectedRole = roleRepo.findByRoleName(roleName);
        if (selectedRole.isPresent()){
            UserRole userRole = new UserRole(user, selectedRole.get());
            userRoleRepo.save(userRole);
        }

    }
    public boolean checkPassword(String rawPassword, String storedPassword) {
        return rawPassword.equals(storedPassword);
    }
    public boolean authenticateUser(String username, String rawPassword) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user != null) {
            if (checkPassword(rawPassword, user.getPassword())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    public void deleteUser(int id){
        userRepo.deleteById(id);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }
    public User findById(int id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
    }

}
