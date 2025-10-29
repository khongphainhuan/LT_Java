package com.pascs.citizen;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Thêm thư viện Toast để test click
import android.content.Intent;

import com.google.android.material.textfield.TextInputEditText; // Dùng cái này
import com.pascs.citizen.R;

public class LoginActivity extends AppCompatActivity {

    // 1. Khai báo các biến UI
    TextInputEditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Nối file layout XML
        setContentView(R.layout.activity_login);

        // 3. Ánh xạ (tìm) các view từ XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 4. (Quan trọng) Xử lý sự kiện click

        // Khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Sau này sẽ gọi API của Mạnh tại đây

                // Tạm thời, chúng ta sẽ chuyển thẳng sang MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                // Gọi finish() để người dùng không thể "Back" lại màn hình Login
                finish();
            }
        });

        // Khi nhấn vào link "Đăng ký"
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TẠO MỘT "Ý ĐỊNH" (INTENT) ĐỂ CHUYỂN MÀN HÌNH
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                // THỰC THI "Ý ĐỊNH" ĐÓ
                startActivity(intent);
            }
        });
    }
}