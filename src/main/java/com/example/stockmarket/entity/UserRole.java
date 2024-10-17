package com.example.stockmarket.entity;

import com.example.stockmarket.CompositePK;
import jakarta.persistence.*;

@Entity
@IdClass(CompositePK.class)
@Table(name = "user_roles")
public class UserRole {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    public UserRole(){

    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
