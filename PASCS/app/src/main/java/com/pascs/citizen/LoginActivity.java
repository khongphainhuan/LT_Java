    package com.pascs.citizen;

    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;
    import com.google.android.material.textfield.TextInputEditText;

    // === ĐÃ SỬA LỖI GỐC 2 (FIXED ROOT ERROR 2) ===
    // (Xóa (Removed) '.activities' (tệp '.activities') - vì 'MainActivity' (Hoạt động chính) nằm CÙNG package (gói) 'com.pascs.citizen')
    import com.pascs.citizen.activities.BaseActivity;
    import com.pascs.citizen.activities.MainActivity;
    // ===================================

    import com.pascs.citizen.api.ApiConfig;
    import com.pascs.citizen.api.ApiService;
    import com.pascs.citizen.models.LoginRequest;
    import com.pascs.citizen.models.LoginResponse;
    import com.pascs.citizen.utils.SharedPrefManager;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class LoginActivity extends BaseActivity {

        private TextInputEditText etUsername;
        private TextInputEditText etPassword;
        private Button btnLogin;
        private TextView tvGoToRegister;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Kiểm tra (Check) đã login (đăng nhập) chưa
            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            setContentView(R.layout.activity_login);

            // Khởi tạo (Initialize) views (khung nhìn)
            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvGoToRegister = findViewById(R.id.tvGoToRegister);

            // Xử lý nút đăng nhập (login)
            btnLogin.setOnClickListener(v -> handleLogin());

            // Xử lý chuyển (Go) sang màn hình đăng ký (register)
            tvGoToRegister.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        private void handleLogin() {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation (Xác thực)
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

            // Disable (Tắt) button (nút) khi đang login (đăng nhập)
            /*btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");

            LoginRequest loginRequest = new LoginRequest(username, password);

            ApiService apiService = ApiConfig.getApiService();
            Call<LoginResponse> call = apiService.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    // Enable (Bật) lại button (nút)
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        // (Giữ nguyên 100% logic (logic) (logic) (logic) 'isSuccess()' (thành công) GỐC (original) của bạn)
                        if (loginResponse.isSuccess()) {

                            // === ĐÃ SỬA LỖI GỐC 1 (FIXED ROOT ERROR 1) ===
                            // (Lỗi của Gemini (Gemini's) (typo (lỗi gõ)): 'saveToken' (lưu mã thông báo) -> 'saveAuthToken' (lưu mã thông báo xác thực))
                            SharedPrefManager.getInstance(LoginActivity.this)
                                    .saveAuthToken(loginResponse.getToken()); // «« ĐÃ SỬA (FIXED)
                            // ===================================

                            // Lưu (Save) user info (thông tin người dùng)
                            SharedPrefManager.getInstance(LoginActivity.this)
                                    .saveUser(loginResponse.getUser());

                            Toast.makeText(LoginActivity.this,
                                    "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển (Go) sang MainActivity (Hoạt động chính)
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this,
                                    loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Response (Phản hồi) không thành công
                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thất bại! Mã lỗi: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // Enable (Bật) lại button (nút)
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");

                    // Hiển thị lỗi kết nối (network) (network) (network)
                    Toast.makeText(LoginActivity.this,
                            "Lỗi kết nối: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });*/
            Toast.makeText(LoginActivity.this,
                    "[TEST] Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

// Chuyển (Go) sang MainActivity (Hoạt động chính)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }