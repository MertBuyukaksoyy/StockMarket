package com.example.stockmarket;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private UserRoleRepo userRoleRepository;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByRoleName("user")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("user");
                    return roleRepository.save(role);
                });

        User adminUser = userRepository.findByUsername("mert")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("mert");
                    user.setPassword("123");
                    userRepository.save(user);
                    return user;
                });

        if (userRoleRepository.findByUserAndRole(adminUser, adminRole).isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setUser(adminUser);
            userRole.setRole(adminRole);
            userRoleRepository.save(userRole);

            System.out.println("Admin kullanıcıya admin rolü atandı.");
        } else {
            System.out.println("Admin kullanıcı zaten admin rolüne sahip.");
        }
    }
    }

