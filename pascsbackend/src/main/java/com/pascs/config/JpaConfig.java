package com.pascs.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    /**
     * Custom Hibernate properties để bỏ qua lỗi khi bảng không tồn tại
     * và cho phép Hibernate tạo bảng từ entity definitions
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            // QUAN TRỌNG: Tắt validation metadata để Hibernate không kiểm tra bảng trước khi tạo
            hibernateProperties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
            // Bỏ qua validation metadata
            hibernateProperties.put("hibernate.jpa.compliance.query", "false");
            // Không dừng khi gặp lỗi
            hibernateProperties.put("hibernate.hbm2ddl.halt_on_error", "false");
            // Chỉ tạo bảng từ entity, không validate từ database
            hibernateProperties.put("hibernate.hbm2ddl.jdbc_metadata_extraction_strategy", "individually");
        };
    }
}

