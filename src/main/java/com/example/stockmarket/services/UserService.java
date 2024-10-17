package com.example.stockmarket.services;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String userName, String password, String roleName){

        User newUser = new User();
        newUser.setUsername(userName);
        newUser.setPassword(passwordEncoder.encode(password));
        Role role = roleRepo.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        userRepo.save(newUser);
        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole(role);
        userRoleRepo.save(userRole);

        return newUser;
    }

}
