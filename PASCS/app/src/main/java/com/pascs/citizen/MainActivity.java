package com.pascs.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pascs.citizen.R;
import com.pascs.citizen.models.User;
import com.pascs.citizen.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private MaterialCardView cardTakeQueue, cardBookAppointment, cardTrackApplication, cardFeedback;
    private FloatingActionButton fabLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        cardTakeQueue = findViewById(R.id.cardTakeQueue);
        cardBookAppointment = findViewById(R.id.cardBookAppointment);
        cardTrackApplication = findViewById(R.id.cardTrackApplication);
        cardFeedback = findViewById(R.id.cardFeedback);
        fabLogout = findViewById(R.id.fabLogout);

        // Lấy (Get) User (Người dùng) và hiển thị Tên (Name)
        User user = SharedPrefManager.getInstance(this).getUser();
        String userName = (user != null) ? user.getFullName() : null;

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Xin chào, " + userName);
        } else {
            tvWelcome.setText("Xin chào, Công dân!");
        }

        // Gán (Set) Click (Nhấn) Listeners (Trình nghe)
        cardTakeQueue.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Lấy số đang phát triển", Toast.LENGTH_SHORT).show();
        });

        cardBookAppointment.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đặt lịch đang phát triển", Toast.LENGTH_SHORT).show();
        });

        cardTrackApplication.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Theo dõi hồ sơ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        cardFeedback.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Phản hồi đang phát triển", Toast.LENGTH_SHORT).show();
        });

        fabLogout.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        SharedPrefManager.getInstance(this).logout();

        Intent intent = new Intent(this, com.pascs.citizen.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }
}