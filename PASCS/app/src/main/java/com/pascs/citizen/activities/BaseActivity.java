package com.pascs.citizen.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// (Đảm bảo file LanguageManager của bạn nằm ở đây)
import com.pascs.citizen.utils.LanguageManager;

/**
 * Đây là Activity CHA.
 * TẤT CẢ các Activity khác (Login, Main, Register, TakeQueue...)
 * sẽ kế thừa (extends) từ file này.
 */
public class BaseActivity extends AppCompatActivity {

    private LanguageManager languageManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Tạo LanguageManager
        LanguageManager tempLanguageManager = new LanguageManager(newBase);
        Context context = tempLanguageManager.updateContext(newBase);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo LanguageManager (cho các hàm helper)
        languageManager = new LanguageManager(this);
    }

    // Helper methods
    // (Các hàm này dùng để gọi từ các Activity con, ví dụ: nút "🌐")
    protected LanguageManager getLanguageManager() {
        if (languageManager == null) {
            languageManager = new LanguageManager(this);
        }
        return languageManager;
    }

    protected void changeLanguage(String languageCode) {
        getLanguageManager().setLanguage(languageCode);
        recreate(); // Tải lại Activity
    }

    protected String getCurrentLanguage() {
        return getLanguageManager().getLanguage();
    }
}