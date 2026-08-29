package com.example.booking.config;

import com.example.booking.entity.AppUser;
import com.example.booking.entity.Resource;
import com.example.booking.entity.Role;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            UserRepository users,
            ResourceRepository resources,
            PasswordEncoder encoder) {

        return args -> {

            if (!users.existsByEmail("admin@example.com")) {
                users.save(
                    new AppUser(
                        "System Admin",
                        "admin@example.com",
                        encoder.encode("Admin@123"),
                        Role.ADMIN
                    )
                );
            }

            if (!users.existsByEmail("user@example.com")) {
                users.save(
                    new AppUser(
                        "Test User",
                        "user@example.com",
                        encoder.encode("User@123"),
                        Role.USER
                    )
                );
            }

            if (resources.count() == 0) {

                resources.save(
                    new Resource(
                        "Conference Room A",
                        "ROOM",
                        "10-seat conference room",
                        new BigDecimal("150.00"),
                        true
                    )
                );

                resources.save(
                    new Resource(
                        "Company Car",
                        "VEHICLE",
                        "Sedan for business travel",
                        new BigDecimal("2500.00"),
                        true
                    )
                );

                resources.save(
                    new Resource(
                        "Projector",
                        "EQUIPMENT",
                        "Full HD meeting projector",
                        new BigDecimal("500.00"),
                        true
                    )
                );
            }
        };
    }
}