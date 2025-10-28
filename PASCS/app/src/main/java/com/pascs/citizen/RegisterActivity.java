package com.pascs.citizen;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etFullName, etEmailRegister, etPasswordRegister;
    Button btnRegister;
    TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ
        etFullName = findViewById(R.id.etFullName);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Xử lý sự kiện
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Gọi API đăng ký của Mạnh
                Toast.makeText(RegisterActivity.this, "Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();
            }
        });

        // Quay về màn hình Đăng nhập
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc (đóng) màn hình này để quay lại màn hình Login
                finish();
            }
        });
    }
}