package com.pascs.citizen; // (Gói GỐC)

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.models.Application;

public class ApplicationDetailActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvApplicationId, tvServiceType, tvStatus, tvSubmitDate, tvCompletedDate, tvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarApplicationDetail);
        tvApplicationId = findViewById(R.id.tvApplicationId);
        tvServiceType = findViewById(R.id.tvServiceType);
        tvStatus = findViewById(R.id.tvStatus);
        tvSubmitDate = findViewById(R.id.tvSubmitDate);
        tvCompletedDate = findViewById(R.id.tvCompletedDate);
        tvNotes = findViewById(R.id.tvNotes);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết hồ sơ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Lấy (Get) application ID từ (from) intent
        int applicationId = getIntent().getIntExtra("APPLICATION_ID", -1);

        // --- Tải (Load) chi tiết (Detail) ---
        loadApplicationDetail(applicationId);
    }

    // (Hàm này để xử lý khi nhấn nút Back trên Toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadApplicationDetail(int id) {
        // Mock data
        Application app = new Application(id, "Cấp Căn cước công dân", "processing", "15/10/2024");
        app.setNotes("Hồ sơ đang được xử lý. Vui lòng chờ thông báo.");

        // Hiển thị (Display) data
        tvApplicationId.setText("#" + app.getId());
        tvServiceType.setText(app.getServiceType());
        tvSubmitDate.setText(app.getSubmitDate());
        tvNotes.setText(app.getNotes());

        // Cài đặt (Set) trạng thái (Status) với màu sắc (Color)
        String status = app.getStatus();
        switch (status) {
            case "submitted":
                tvStatus.setText("Đã gửi");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "processing":
                tvStatus.setText("Đang xử lý");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "need_supplement":
                tvStatus.setText("Cần bổ sung");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvNotes.setText("⚠️ Vui lòng bổ sung: Bản sao CCCD, Giấy khai sinh gốc");
                break;
            case "completed":
                tvStatus.setText("Hoàn thành");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tvCompletedDate.setText("Hoàn thành: 20/10/2024");
                break;
        }
    }
}