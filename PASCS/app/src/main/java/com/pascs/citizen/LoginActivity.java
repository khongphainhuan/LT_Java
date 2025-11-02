package com.pascs.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import com.pascs.citizen.api.ApiConfig;
import com.pascs.citizen.api.ApiService;
import com.pascs.citizen.models.LoginRequest;
import com.pascs.citizen.models.LoginResponse;
import com.pascs.citizen.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra đã login chưa
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Đã login → Chuyển sang MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Khởi tạo views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Xử lý nút đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Xử lý chuyển sang màn hình đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đăng ký đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Disable button khi đang login
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // Tạo request object
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Gọi API
        ApiService apiService = ApiConfig.getApiService();
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Enable lại button
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // Lưu token
                        SharedPrefManager.getInstance(LoginActivity.this)
                                .saveToken(loginResponse.getToken());

                        // Lưu user info
                        SharedPrefManager.getInstance(LoginActivity.this)
                                .saveUser(loginResponse.getUser());

                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this,
                                loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response không thành công
                    Toast.makeText(LoginActivity.this,
                            "Đăng nhập thất bại! Mã lỗi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Enable lại button
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                // Hiển thị lỗi kết nối
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}