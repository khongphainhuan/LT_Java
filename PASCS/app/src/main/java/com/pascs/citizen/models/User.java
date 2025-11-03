package com.pascs.citizen.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model (Mô hình) POJO (Mô hình) cho đối tượng User (Người dùng).
 * "Khớp" (Match) 100% với Backend (Phần sau) API (API) (API) (API) và SharedPrefManager (Trình quản lý SharedPref).
 */
public class User {

    // Dùng @SerializedName để "khớp" (match) 100% với JSON (Dữ liệu JSON) của Backend (Phần sau)
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName; // Dùng cho Giao diện (UI) (UI) (Giao diện (UI)) Đăng ký (Register)

    // Hàm (Function) tạo (Constructor) (dùng cho SharedPrefManager (Trình quản lý SharedPref))
    public User(int id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    // Getters (Trình lấy) (Cần thiết cho Giao diện (UI) (UI) (Giao diện (UI)) & Logic (Logic) (Logic) (Logic))
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}