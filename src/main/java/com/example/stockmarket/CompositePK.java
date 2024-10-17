package com.example.stockmarket;

import java.io.Serializable;
import java.util.Objects;

public class CompositePK implements Serializable {
    private int user;
    private int role;

    public CompositePK() {
    }

    public CompositePK(int user, int role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositePK that = (CompositePK) o;
        return Objects.equals(user, that.user) && Objects.equals(role, that.role);
    }


    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
