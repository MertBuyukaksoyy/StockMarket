package com.example.stockmarket.security;
import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.User;
import com.example.stockmarket.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<UserRole> userRoles = userRoleRepo.findByUser(user);

        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }


}