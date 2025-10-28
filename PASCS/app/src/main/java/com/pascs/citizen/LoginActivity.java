package com.pascs.citizen.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Thêm thư viện Toast để test click

import com.google.android.material.textfield.TextInputEditText; // Dùng cái này
import com.pascs.citizen.R;

public class LoginActivity extends AppCompatActivity {

    // 1. Khai báo các biến UI
    TextInputEditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Nối file layout XML
        setContentView(R.layout.activity_login);

        // 3. Ánh xạ (tìm) các view từ XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 4. (Quan trọng) Xử lý sự kiện click

        // Khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạm thời hiển thị thông báo
                Toast.makeText(LoginActivity.this, "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();

                // TODO: Sau này sẽ gọi API của Mạnh tại đây
                // String email = etEmail.getText().toString();
                // String password = etPassword.getText().toString();
                // callLoginApi(email, password);
            }
        });

        // Khi nhấn vào link "Đăng ký"
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạm thời hiển thị thông báo
                Toast.makeText(LoginActivity.this, "Chuyển sang màn hình Đăng ký", Toast.LENGTH_SHORT).show();

                // TODO: Sau này sẽ tạo RegisterActivity và chuyển màn hình
                // Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                // startActivity(intent);
            }
        });
    }
}