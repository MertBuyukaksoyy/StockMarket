package com.example.stockmarket.dao;

import com.example.stockmarket.CompositePK;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepo extends JpaRepository<UserRole, CompositePK> {
    Optional<UserRole> findByUserAndRole(User user, Role role);

    List<UserRole> findByUser(User user);
}
