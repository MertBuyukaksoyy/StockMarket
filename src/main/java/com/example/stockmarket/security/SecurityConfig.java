package com.example.stockmarket.security;

import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.services.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
public class SecurityConfig {

    @Autowired
    public CustomUserService customUserService;

  /*  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService);
    }
    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery(
                "SELECT username, password, enabled FROM users WHERE username = ?"
        );

        // Roller (authority) sorgusu
        userDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT u.username, r.role_name " +
                        "FROM users u " +
                        "JOIN user_roles ur ON u.user_id = ur.user_id " +
                        "JOIN roles r ON ur.role_id = r.role_id " +
                        "WHERE u.username = ?"
        );

        return userDetailsManager;
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/user/authenticateUser")
                        .defaultSuccessUrl("/home",true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )



                .logout(logout -> logout
                        .logoutUrl("/login")
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }
}

