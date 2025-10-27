package com.pascs.config;

import com.pascs.model.User;
import com.pascs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Tạo user admin mẫu nếu chưa tồn tại
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@pascs.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }

        // Tạo user staff mẫu
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setEmail("staff@pascs.com");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setFullName("Nguyễn Văn Nhân viên");
            staff.setRole(User.UserRole.STAFF);
            userRepository.save(staff);
            System.out.println("✅ Staff user created: staff / staff123");
        }

        // Tạo user citizen mẫu
        if (userRepository.findByUsername("citizen").isEmpty()) {
            User citizen = new User();
            citizen.setUsername("citizen");
            citizen.setEmail("citizen@example.com");
            citizen.setPassword(passwordEncoder.encode("citizen123"));
            citizen.setFullName("Trần Văn Công dân");
            citizen.setRole(User.UserRole.CITIZEN);
            citizen.setPriorityEligible(true);
            userRepository.save(citizen);
            System.out.println("✅ Citizen user created: citizen / citizen123");
        }
    }
}