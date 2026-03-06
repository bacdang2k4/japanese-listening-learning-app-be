package com.jp.be_jplearning.config;

import com.jp.be_jplearning.entity.Admin;
import com.jp.be_jplearning.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            log.info("No admin found in database. Creating default admin...");
            Admin defaultAdmin = new Admin();
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
            defaultAdmin.setCreatedAt(LocalDateTime.now());

            adminRepository.save(defaultAdmin);
            log.info("Default admin created successfully with username 'admin'");
        } else {
            log.info("Admin already exists in the database. Skipping creation of default admin.");
        }
    }
}
