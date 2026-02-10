package com.quotation.config;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.quotation.model.Admin;
import com.quotation.repository.AdminRepository;

@Configuration
public class AdminDataLoader {

    @Bean
    CommandLineRunner loadAdmin(AdminRepository adminRepository) {
        return args -> {

            if (adminRepository.findByUsername("admin").isEmpty()) {
                Admin admin = new Admin("admin", "admin123");
                adminRepository.save(admin);
                System.out.println("✅ Admin created in MongoDB");
            } else {
                System.out.println("ℹ Admin already exists");
            }
        };
    }
}
