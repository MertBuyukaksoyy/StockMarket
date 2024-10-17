package com.example.stockmarket.services;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Transactional
    public User createUser(String userName, String password, String roleName){

        User newUser = new User();
        newUser.setUsername(userName);
        newUser.setPassword(password);
        Role role = roleRepo.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        userRepo.save(newUser);
        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole(role);
        userRoleRepo.save(userRole);

        return newUser;
    }public boolean checkPassword(String rawPassword, String storedPassword) {
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


}
