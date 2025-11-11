package com.pascs.citizen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageManager {

    private static final String PREFS_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String LANGUAGE_VIETNAMESE = "vi";
    private static final String LANGUAGE_ENGLISH = "en";

    private Context context;
    private SharedPreferences prefs;

    public LanguageManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Lưu (Save) ngôn ngữ (Language) đã chọn
    public void setLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
        updateResources(languageCode);
    }

    // Lấy (Get) ngôn ngữ (Language) đã lưu
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_VIETNAMESE); // Mặc định (Default) Tiếng Việt
    }

    // Áp dụng (Apply) ngôn ngữ (Language)
    public void updateResources(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        context.createConfigurationContext(config);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Load ngôn ngữ (Language) đã lưu khi khởi động (Start) app
    public void loadSavedLanguage() {
        String savedLanguage = getLanguage();
        updateResources(savedLanguage);
    }

    // Check xem có phải Tiếng Việt không
    public boolean isVietnamese() {
        return getLanguage().equals(LANGUAGE_VIETNAMESE);
    }

    // Check xem có phải English không
    public boolean isEnglish() {
        return getLanguage().equals(LANGUAGE_ENGLISH);
    }

    // Constants
    public static String getVietnameseCode() {
        return LANGUAGE_VIETNAMESE;
    }

    public static String getEnglishCode() {
        return LANGUAGE_ENGLISH;
    }
}