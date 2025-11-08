package com.pascs.config;

import com.pascs.model.User;
import com.pascs.model.Service; // THÊM IMPORT
import com.pascs.repository.UserRepository;
import com.pascs.repository.ServiceRepository; // THÊM IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServiceRepository serviceRepository; // THÊM DÒNG NÀY

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting data initialization...");

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

        // Tạo thêm 1 citizen nữa để test
        if (userRepository.findByUsername("hongphuc").isEmpty()) {
            User citizen2 = new User();
            citizen2.setUsername("hongphuc");
            citizen2.setEmail("hongphuc@example.com");
            citizen2.setPassword(passwordEncoder.encode("123456"));
            citizen2.setFullName("Trần Hồng Phúc");
            citizen2.setRole(User.UserRole.CITIZEN);
            citizen2.setPriorityEligible(false);
            userRepository.save(citizen2);
            System.out.println("✅ Citizen user created: hongphuc / 123456");
        }

        // Tạo dịch vụ mẫu
        if (serviceRepository.count() == 0) {
            Service service1 = new Service();
            service1.setCode("KHAI_SINH");
            service1.setName("Đăng ký khai sinh");
            service1.setDescription("Đăng ký khai sinh cho trẻ em dưới 1 tuổi");
            service1.setRequiredDocuments("Giấy chứng sinh, CMND của bố mẹ");
            service1.setProcessingTime(5);
            service1.setFee(0.0);
            serviceRepository.save(service1);

            Service service2 = new Service();
            service2.setCode("CCCD");
            service2.setName("Cấp đổi CCCD");
            service2.setDescription("Cấp mới, đổi CCCD");
            service2.setRequiredDocuments("CMND cũ, ảnh 3x4");
            service2.setProcessingTime(7);
            service2.setFee(50000.0);
            serviceRepository.save(service2);

            System.out.println("✅ Sample services created");
        }

        System.out.println("🎉 Sample data initialization completed!");
        System.out.println("📊 Total users in database: " + userRepository.count());
        System.out.println("📊 Total services in database: " + serviceRepository.count());
    }
}