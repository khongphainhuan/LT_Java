package com.pascs.citizen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageManager {

    private static final String PREFS_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    public static final String LANGUAGE_VIETNAMESE = "vi";
    public static final String LANGUAGE_ENGLISH = "en";

    private SharedPreferences prefs;

    public LanguageManager(Context context) {
        // (Sửa lại: Dùng SharedPreferences chuẩn của file bạn, không phải 'PreferenceManager')
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * Lấy (Get) ngôn ngữ (Language) đã lưu
     */
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_VIETNAMESE); // Mặc định (Default) Tiếng Việt
    }

    public Context updateContext(Context context) {
        String language = getLanguage();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }


    // public void updateResources(String languageCode) { ... }


    public void loadSavedLanguage() {
        // (Không làm gì cả)
    }

    // Các hàm Helper (OK)
    public boolean isVietnamese() {
        return getLanguage().equals(LANGUAGE_VIETNAMESE);
    }

    public boolean isEnglish() {
        return getLanguage().equals(LANGUAGE_ENGLISH);
    }

    public static String getVietnameseCode() {
        return LANGUAGE_VIETNAMESE;
    }

    public static String getEnglishCode() {
        return LANGUAGE_ENGLISH;
    }
}