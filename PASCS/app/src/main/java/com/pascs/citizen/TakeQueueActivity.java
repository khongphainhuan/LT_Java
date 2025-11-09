package com.pascs.citizen; // (Gói GỐC, khớp với cấu trúc của bạn)

import android.content.Intent; // (Cần cho file Hướng dẫn)
import android.os.Bundle;
import android.view.MenuItem; // (Quan trọng cho nút Back)
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // (Quan trọng cho Toolbar)

// (Các file models và api bạn sẽ cần khi có backend)
// import com.pascs.citizen.api.ApiConfig;
// import com.pascs.citizen.api.ApiService;
// import com.pascs.citizen.models.Service;
// import com.pascs.citizen.models.QueueTicketRequest;
// import com.pascs.citizen.models.QueueTicketResponse;

// (Import các file từ package GỐC - RẤT QUAN TRỌNG)
import com.pascs.citizen.activities.MainActivity; // (Vì Main của bạn nằm trong 'activities')
// import com.pascs.citizen.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class TakeQueueActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinnerServices;
    private TextView tvQueueInfo;
    private Button btnConfirmTakeQueue;

    // (Biến này sẽ chứa danh sách dịch vụ thật từ API)
    // private List<Service> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_queue);

        // Khởi tạo Views
        toolbar = findViewById(R.id.toolbarTakeQueue);
        spinnerServices = findViewById(R.id.spinnerServices);
        tvQueueInfo = findViewById(R.id.tvQueueInfo);
        btnConfirmTakeQueue = findViewById(R.id.btnConfirmTakeQueue);

        // --- Cài đặt Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lấy số thứ tự");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút Back
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Tải (Load) Dịch vụ (TẠM THỜI DÙNG DATA GIẢ) ---
        loadServices_FakeData();

        // --- Xử lý sự kiện ---
        btnConfirmTakeQueue.setOnClickListener(v -> handleConfirmTakeQueue());
    }

    // (Hàm này để xử lý khi nhấn nút Back trên Toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity này và quay lại MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadServices_FakeData() {
        // (Đây là data giả vì chưa có backend của Mạnh)
        List<String> fakeServiceNames = new ArrayList<>();
        fakeServiceNames.add("Dịch vụ Cấp Căn cước công dân");
        fakeServiceNames.add("Đăng ký Giấy phép lái xe");
        fakeServiceNames.add("Đăng ký tạm trú, tạm vắng");

        // Tạo Adapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                fakeServiceNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerServices.setAdapter(adapter);

        tvQueueInfo.setText("Hiện có: 5 người đang chờ."); // (Data giả)
    }

    private void handleConfirmTakeQueue() {
        // (Đây là nơi bạn sẽ gọi API của Mạnh để "Lấy số")
        String selectedService = spinnerServices.getSelectedItem().toString();

        // (Hiện tại, chúng ta chỉ hiển thị thông báo TEST)
        btnConfirmTakeQueue.setEnabled(false);
        btnConfirmTakeQueue.setText("Đang xử lý...");

        Toast.makeText(this,
                "[TEST] Đã gửi yêu cầu lấy số cho: " + selectedService,
                Toast.LENGTH_LONG).show();

        // (Sau 2 giây thì giả lập là thành công và đóng màn hình)
        new android.os.Handler().postDelayed(
                () -> {
                    Toast.makeText(this,
                            "[TEST] Lấy số thành công!",
                            Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình này
                },
                2000
        );
    }
}

