package com.pascs.citizen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.pascs.citizen.activities.BaseActivity;

// (TODO: Bạn sẽ cần import các model và ApiService khi kết nối backend)
// import com.pascs.citizen.api.ApiConfig;
// import com.pascs.citizen.api.ApiService;
// import com.pascs.citizen.models.RegisterRequest;
// import com.pascs.citizen.models.RegisterResponse;
// import retrofit2.Call;
// import retrofit2.Callback;
// import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etRegisterUsername;
    private TextInputEditText etRegisterPassword;
    private TextInputEditText etRegisterConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo views
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Xử lý nút Đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());

        // Xử lý nút Quay lại Đăng nhập
        tvGoToLogin.setOnClickListener(v -> {
            // Chỉ cần đóng màn hình này lại để quay về LoginActivity
            finish();
        });
    }

    private void handleRegister() {
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

        // === BẮT ĐẦU PHẦN VALIDATION (Bạn có thể làm ngay) ===
        if (username.isEmpty()) {
            etRegisterUsername.setError("Tên đăng nhập không được để trống");
            etRegisterUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etRegisterPassword.setError("Mật khẩu không được để trống");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etRegisterPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etRegisterConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            etRegisterConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.setError("Mật khẩu không trùng khớp");
            etRegisterConfirmPassword.requestFocus();
            return;
        }
        // === KẾT THÚC PHẦN VALIDATION ===

        // Nếu mọi thứ hợp lệ, chúng ta sẽ gọi API
        // (Đây là phần chờ backend của Mạnh)

        Toast.makeText(this, "[TEST] Validation thành công. Sẵn sàng gọi API...", Toast.LENGTH_SHORT).show();

        // (TODO: Mở comment phần dưới khi có backend)
        /*
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang xử lý...");

        RegisterRequest registerRequest = new RegisterRequest(username, password);
        ApiService apiService = ApiConfig.getApiService();
        Call<RegisterResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng ký");

                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi đăng ký thành công
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình đăng nhập
                } else {
                    // Xử lý khi có lỗi từ server
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng ký");
                // Xử lý khi lỗi kết nối
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }
}
