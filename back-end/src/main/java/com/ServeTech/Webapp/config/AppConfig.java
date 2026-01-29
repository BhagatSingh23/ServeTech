package com.ServeTech.Webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Will be used to configure additional application-wide beans
@Configuration
@EnableScheduling
public class AppConfig {
    // Additional application-wide configurations can go here

    // password encoder to use it in auth service
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}