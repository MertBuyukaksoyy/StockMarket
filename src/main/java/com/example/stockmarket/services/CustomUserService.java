package com.example.stockmarket.services;
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
        // Kullanıcıyı veritabanından bul
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Kullanıcının rollerini almak için gerekli işlemler
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Kullanıcının rollerini UserRole tablosundan al
        List<UserRole> userRoles = userRoleRepo.findByUser(user);

        for (UserRole userRole : userRoles) {
            // Kullanıcı rolünü authority listesine ekle
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName().toUpperCase()));
        }

        // UserDetails nesnesini oluştur
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),  // Kullanıcının hashlenmiş şifresi
                authorities
        );
    }
}


