package com.pascs.citizen.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pascs.citizen.utils.LanguageManager;

public class BaseActivity extends AppCompatActivity {

    private LanguageManager languageManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Tạo LanguageManager
        LanguageManager languageManager = new LanguageManager(newBase);

        // Cập nhật Context với ngôn ngữ đã chọn
        Context context = languageManager.updateContext(newBase);

        // Gọi super với Context đã được cập nhật
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo LanguageManager
        languageManager = new LanguageManager(this);
        languageManager.loadSavedLanguage();
    }

    // Helper methods
    protected LanguageManager getLanguageManager() {
        if (languageManager == null) {
            languageManager = new LanguageManager(this);
        }
        return languageManager;
    }

    protected void changeLanguage(String languageCode) {
        getLanguageManager().setLanguage(languageCode);
        recreate(); // Reload Activity
    }

    protected String getCurrentLanguage() {
        return getLanguageManager().getLanguage();
    }
}