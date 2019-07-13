package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DbInit implements CommandLineRunner {
    private UserDao dao;
    private PasswordEncoder passwordEncoder;

    public DbInit(UserDao dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // If an "admin" user does not exist, create it.
//        if (dao.findByUsername("admin") == null)
//        {
//            User admin = new User();
//            admin.setUsername("admin");
//            admin.setPassword(passwordEncoder.encode("admin123"));
//            admin.setRole("ADMIN");
//            admin.setActive(true);
//
//            dao.save(admin);
//        }

    }
}
