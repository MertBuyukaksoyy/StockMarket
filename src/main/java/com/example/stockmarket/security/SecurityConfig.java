package com.example.stockmarket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public CustomUserService customUserService;
    @Autowired
    public SecurityConfig(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

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
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                )
                .userDetailsService(customUserService);

        return http.build();
    }

 /*   @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return AuthenticationConfiguration.getAuthenticationManager();
    }*/
}

