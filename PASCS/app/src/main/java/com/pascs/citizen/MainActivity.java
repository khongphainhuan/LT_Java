package com.pascs.citizen; // Đảm bảo package đúng

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Dùng để test click

public class MainActivity extends AppCompatActivity {

    // 1. Khai báo các biến UI
    TextView tvWelcome;
    Button btnGetQueueNumber, btnBookAppointment;

    // (Chúng ta cũng có thể khai báo Toolbar ở đây nếu cần)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Nối file layout XML
        setContentView(R.layout.activity_main);
        // (Lưu ý: code cũ của bạn có thể đã có dòng này)

        // 3. Ánh xạ (tìm) các view từ XML
        tvWelcome = findViewById(R.id.tvWelcome);
        btnGetQueueNumber = findViewById(R.id.btnGetQueueNumber);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        // TODO: Chúng ta sẽ lấy tên thật của công dân và cập nhật tvWelcome
        // tvWelcome.setText("Chào mừng, Nhuần!");

        // 4. Xử lý sự kiện click

        // Khi nhấn nút "Lấy số thứ tự"
        btnGetQueueNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Sau này sẽ chuyển sang màn hình Lấy số
                Toast.makeText(MainActivity.this, "Chức năng Lấy số thứ tự", Toast.LENGTH_SHORT).show();
            }
        });

        // Khi nhấn nút "Đặt lịch hẹn"
        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Sau này sẽ chuyển sang màn hình Đặt lịch
                Toast.makeText(MainActivity.this, "Chức năng Đặt lịch hẹn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}