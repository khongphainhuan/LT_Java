package com.pascs.citizen.activities; // (Đảm bảo package (gói) khớp (match) 100%)

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pascs.citizen.LoginActivity;
import com.pascs.citizen.R;
import com.pascs.citizen.TakeQueueActivity;
import com.pascs.citizen.models.User;
import com.pascs.citizen.utils.SharedPrefManager;

// (Lỗi "cannot find symbol MainActivity" (không tìm thấy biểu tượng MainActivity) sẽ 100% biến mất sau khi file (tệp) này được "Build" (Xây dựng) thành công)
public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private MaterialCardView cardTakeQueue, cardQueueStatus, cardBookAppointment, cardTrackApplication, cardFeedback;
    private FloatingActionButton fabLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // (File (Tệp) này sẽ CẦN file (tệp) 'activity_main.xml' (Bố cục Hoạt động chính) (sử dụng CardViews (Khung nhìn Thẻ)))
        // (Nếu bạn chưa có file (tệp) XML (XML) đó, nó sẽ báo lỗi ở dòng 'setContentView' (đặt khung nhìn nội dung))
        setContentView(R.layout.activity_main);

        // (Kiểm tra (Check) xem User (Người dùng) có "nên" (should) ở đây không,
        //  hay họ "vô tình" (accidentally) mở (open) màn hình này mà chưa login (đăng nhập)?)
        /*if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Chưa login (đăng nhập) → "Đá" (Kick) họ về LoginActivity (Hoạt động Đăng nhập)
            handleLogout(); // (Hàm (Function) Logout (Đăng xuất) (của bạn) cũng làm việc này)
            return;
        }*/

        tvWelcome = findViewById(R.id.tvWelcome);
        cardTakeQueue = findViewById(R.id.cardTakeQueue);
        cardQueueStatus = findViewById(R.id.cardQueueStatus);
        cardBookAppointment = findViewById(R.id.cardBookAppointment);
        cardTrackApplication = findViewById(R.id.cardTrackApplication);
        cardFeedback = findViewById(R.id.cardFeedback);
        fabLogout = findViewById(R.id.fabLogout);
        // Gán sự kiện cho nút Đăng xuất
        fabLogout.setOnClickListener(v -> handleLogout());

        // === ĐÃ SỬA LỖI (FIXED) (Sửa lỗi "Không Khớp" (Mismatch) (getUserName (lấy tên người dùng))) ===
        // Lấy (Get) đối tượng (Object) User (Người dùng) (từ SharedPrefManager (Trình quản lý SharedPref))
        User user = SharedPrefManager.getInstance(this).getUser();
        String userName = (user != null) ? user.getFullName() : null; // Lấy (Get) fullName (Họ và Tên)

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Xin chào, " + userName);
        } else {
            // (Thêm dòng "dự phòng" (fallback) phòng khi tên (name) bị "null" (rỗng))
            tvWelcome.setText("Xin chào, Công dân!");
        }
        // ===================================

        // Gán (Set) Click (Nhấn) Listeners (Trình nghe)
        cardTakeQueue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TakeQueueActivity.class);
            startActivity(intent);
        });

        cardQueueStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    com.pascs.citizen.QueueStatusActivity.class);
            startActivity(intent);
        });

        cardBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    com.pascs.citizen.BookAppointmentActivity.class);
            startActivity(intent);
        });

        cardTrackApplication.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    com.pascs.citizen.ApplicationListActivity.class);
            startActivity(intent);
        });

        cardFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    com.pascs.citizen.FeedbackActivity.class);
            startActivity(intent);
        });

        fabLogout.setOnClickListener(v -> handleLogout());
    }

    // Hàm xử lý đăng xuất
    private void handleLogout() {

        // (QUAN TRỌNG NHẤT) Xóa toàn bộ thông tin đã lưu
        // (Bao gồm token và thông tin user)
        SharedPrefManager.getInstance(this).logout();

        // Hiển thị thông báo
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Mở lại màn hình Login
        Intent intent = new Intent(this, LoginActivity.class);

        // (Quan trọng) Các "cờ" (flags) này sẽ xóa hết các màn hình cũ
        // và đảm bảo người dùng không thể nhấn "Back" để quay lại
        // MainActivity sau khi đã đăng xuất.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // Đóng màn hình MainActivity lại
        finish();
    }
}