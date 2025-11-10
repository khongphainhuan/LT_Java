package com.pascs.config;

import com.pascs.model.*;
import com.pascs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CounterRepository counterRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting data initialization...");

        // Initialize roles
        initializeRoles();

        // Initialize admin user
        initializeAdminUser();

        // Initialize staff user
        initializeStaffUser();

        // Initialize citizen users
        initializeCitizenUsers();

        // Initialize services
        initializeServices();

        // Initialize counters
        initializeCounters();

        System.out.println("🎉 Sample data initialization completed!");
        printSampleCredentials();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(User.UserRole.ADMIN));
            roleRepository.save(new Role(User.UserRole.STAFF));
            roleRepository.save(new Role(User.UserRole.CITIZEN));
            System.out.println("✅ Roles initialized");
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@pascs.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole(User.UserRole.ADMIN);
            admin.setPhoneNumber("0123456789");
            admin.setAddress("UBND Phường");
            admin.setPriorityEligible(false);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }
    }

    private void initializeStaffUser() {
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setEmail("staff@pascs.com");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setFullName("Nguyễn Văn Nhân viên");
            staff.setRole(User.UserRole.STAFF);
            staff.setPhoneNumber("0987654321");
            staff.setAddress("UBND Phường - Quầy số 1");
            staff.setPriorityEligible(false);
            staff.setEnabled(true);
            userRepository.save(staff);
            System.out.println("✅ Staff user created: staff / staff123");
        }

        // Create additional staff members
        if (userRepository.findByUsername("staff2").isEmpty()) {
            User staff2 = new User();
            staff2.setUsername("staff2");
            staff2.setEmail("staff2@pascs.com");
            staff2.setPassword(passwordEncoder.encode("staff123"));
            staff2.setFullName("Trần Thị Cán bộ");
            staff2.setRole(User.UserRole.STAFF);
            staff2.setPhoneNumber("0912345678");
            staff2.setAddress("UBND Phường - Quầy số 2");
            staff2.setPriorityEligible(false);
            staff2.setEnabled(true);
            userRepository.save(staff2);
            System.out.println("✅ Staff user 2 created: staff2 / staff123");
        }
    }

    private void initializeCitizenUsers() {
        if (userRepository.findByUsername("citizen").isEmpty()) {
            User citizen = new User();
            citizen.setUsername("citizen");
            citizen.setEmail("citizen@example.com");
            citizen.setPassword(passwordEncoder.encode("citizen123"));
            citizen.setFullName("Trần Văn Công dân");
            citizen.setRole(User.UserRole.CITIZEN);
            citizen.setPhoneNumber("0934567890");
            citizen.setAddress("123 Đường ABC, Phường XYZ");
            citizen.setPriorityEligible(true); // Elderly citizen
            citizen.setEnabled(true);
            userRepository.save(citizen);
            System.out.println("✅ Citizen user created: citizen / citizen123");
        }

        if (userRepository.findByUsername("hongphuc").isEmpty()) {
            User citizen2 = new User();
            citizen2.setUsername("hongphuc");
            citizen2.setEmail("hongphuc@example.com");
            citizen2.setPassword(passwordEncoder.encode("123456"));
            citizen2.setFullName("Trần Hồng Phúc");
            citizen2.setRole(User.UserRole.CITIZEN);
            citizen2.setPhoneNumber("0945678901");
            citizen2.setAddress("456 Đường DEF, Phường XYZ");
            citizen2.setPriorityEligible(false);
            citizen2.setEnabled(true);
            userRepository.save(citizen2);
            System.out.println("✅ Citizen user created: hongphuc / 123456");
        }

        if (userRepository.findByUsername("thanhlong").isEmpty()) {
            User citizen3 = new User();
            citizen3.setUsername("thanhlong");
            citizen3.setEmail("thanhlong@example.com");
            citizen3.setPassword(passwordEncoder.encode("123456"));
            citizen3.setFullName("Lê Thành Long");
            citizen3.setRole(User.UserRole.CITIZEN);
            citizen3.setPhoneNumber("0956789012");
            citizen3.setAddress("789 Đường GHI, Phường XYZ");
            citizen3.setPriorityEligible(true); // Disabled citizen
            citizen3.setEnabled(true);
            userRepository.save(citizen3);
            System.out.println("✅ Citizen user created: thanhlong / 123456");
        }
    }

    private void initializeServices() {
        if (serviceRepository.count() == 0) {
            // Service 1: CCCD
            Service service1 = new Service();
            service1.setCode("CCCD");
            service1.setName("Cấp căn cước công dân");
            service1.setDescription("Cấp mới, cấp đổi, cấp lại căn cước công dân");
            service1.setRequiredDocuments("CMND cũ (nếu có), Ảnh 4x6, Tờ khai theo mẫu");
            service1.setProcessingTime(7);
            service1.setFee(0.0);
            service1.setStatus(Service.ServiceStatus.ACTIVE);
            serviceRepository.save(service1);

            // Service 2: Hộ khẩu
            Service service2 = new Service();
            service2.setCode("HK");
            service2.setName("Đăng ký hộ khẩu");
            service2.setDescription("Đăng ký thường trú, tạm trú, thay đổi hộ khẩu");
            service2.setRequiredDocuments("Giấy tờ nhà ở, CMND/CCCD, Ảnh 3x4");
            service2.setProcessingTime(3);
            service2.setFee(0.0);
            service2.setStatus(Service.ServiceStatus.ACTIVE);
            serviceRepository.save(service2);

            // Service 3: Khai sinh
            Service service3 = new Service();
            service3.setCode("KS");
            service3.setName("Khai sinh");
            service3.setDescription("Đăng ký khai sinh cho trẻ em");
            service3.setRequiredDocuments("Giấy chứng sinh, CMND của cha mẹ, Giấy đăng ký kết hôn");
            service3.setProcessingTime(1);
            service3.setFee(0.0);
            service3.setStatus(Service.ServiceStatus.ACTIVE);
            serviceRepository.save(service3);

            // Service 4: Tạm trú
            Service service4 = new Service();
            service4.setCode("TT");
            service4.setName("Đăng ký tạm trú");
            service4.setDescription("Đăng ký tạm trú cho người ngoại tỉnh");
            service4.setRequiredDocuments("CMND/CCCD, Giấy tờ thuê nhà, Ảnh 3x4");
            service4.setProcessingTime(2);
            service4.setFee(0.0);
            service4.setStatus(Service.ServiceStatus.ACTIVE);
            serviceRepository.save(service4);

            // Service 5: Kết hôn
            Service service5 = new Service();
            service5.setCode("KH");
            service5.setName("Đăng ký kết hôn");
            service5.setDescription("Đăng ký kết hôn theo quy định pháp luật");
            service5.setRequiredDocuments("CMND/CCCD, Giấy xác nhận tình trạng hôn nhân, Ảnh 3x4");
            service5.setProcessingTime(5);
            service5.setFee(0.0);
            service5.setStatus(Service.ServiceStatus.ACTIVE);
            serviceRepository.save(service5);

            System.out.println("✅ 5 sample services created");
        }
    }

    private void initializeCounters() {
        if (counterRepository.count() == 0) {
            // Get staff users for assignment
            User staff1 = userRepository.findByUsername("staff").orElse(null);
            User staff2 = userRepository.findByUsername("staff2").orElse(null);

            // Counter 1
            Counter counter1 = new Counter();
            counter1.setName("Quầy số 1");
            counter1.setLocation("Tầng 1 - Khu A");
            counter1.setActive(true);
            counter1.setCurrentStaff(staff1);
            counter1.setCurrentQueueNumber("T001");
            counterRepository.save(counter1);

            // Counter 2
            Counter counter2 = new Counter();
            counter2.setName("Quầy số 2");
            counter2.setLocation("Tầng 1 - Khu A");
            counter2.setActive(true);
            counter2.setCurrentStaff(staff2);
            counter2.setCurrentQueueNumber("T002");
            counterRepository.save(counter2);

            // Counter 3 (inactive)
            Counter counter3 = new Counter();
            counter3.setName("Quầy số 3");
            counter3.setLocation("Tầng 1 - Khu B");
            counter3.setActive(false);
            counter3.setCurrentStaff(null);
            counter3.setCurrentQueueNumber(null);
            counterRepository.save(counter3);

            // Counter 4 (priority counter)
            Counter counter4 = new Counter();
            counter4.setName("Quầy ưu tiên");
            counter4.setLocation("Tầng 1 - Khu ưu tiên");
            counter4.setActive(true);
            counter4.setCurrentStaff(null);
            counter4.setCurrentQueueNumber("P001");
            counterRepository.save(counter4);

            System.out.println("✅ 4 sample counters created");
        }
    }

    private void printSampleCredentials() {
        System.out.println("\n📊 ===== SAMPLE CREDENTIALS =====");
        System.out.println("👨‍💼 ADMIN:     admin / admin123");
        System.out.println("👨‍💼 STAFF:     staff / staff123");
        System.out.println("👨‍💼 STAFF 2:   staff2 / staff123");
        System.out.println("👤 CITIZEN:   citizen / citizen123");
        System.out.println("👤 CITIZEN 2: hongphuc / 123456");
        System.out.println("👤 CITIZEN 3: thanhlong / 123456");
        System.out.println("📈 Total users in database: " + userRepository.count());
        System.out.println("🏪 Total services: " + serviceRepository.count());
        System.out.println("🔢 Total counters: " + counterRepository.count());
        System.out.println("================================\n");
        
        System.out.println("🌐 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("📚 API Docs: http://localhost:8080/api-docs");
        System.out.println("🚀 Application ready at: http://localhost:8080");
    }
}