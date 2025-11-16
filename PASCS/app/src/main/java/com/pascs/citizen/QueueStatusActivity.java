package com.pascs.citizen; // (Gói GỐC)

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.models.QueueTicket;

public class QueueStatusActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvTicketNumber, tvServiceName, tvStatus, tvPeopleAhead, tvEstimatedTime;
    private CardView cardQueueInfo;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable updateRunnable;

    // Mock data - Thay bằng API sau
    private QueueTicket currentTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_status);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarQueueStatus);
        tvTicketNumber = findViewById(R.id.tvTicketNumber);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvStatus = findViewById(R.id.tvStatus);
        tvPeopleAhead = findViewById(R.id.tvPeopleAhead);
        tvEstimatedTime = findViewById(R.id.tvEstimatedTime);
        cardQueueInfo = findViewById(R.id.cardQueueInfo);
        progressBar = findViewById(R.id.progressBar);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Theo dõi hàng chờ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Tải (Load) dữ liệu (Data) giả (Mock) ---
        loadMockQueueData();

        // --- Bắt đầu (Start) cập nhật (Update) theo thời gian thực (Real-time) ---
        startRealtimeUpdates();
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

    private void loadMockQueueData() {
        // Mock data - Giả lập (Simulate) đã lấy số thứ tự
        currentTicket = new QueueTicket(
                42,
                "Cấp Căn cước công dân",
                "waiting",
                5,
                "15 phút",
                "10:30 AM"
        );

        updateUI();
    }

    private void updateUI() {
        tvTicketNumber.setText("Số thứ tự: A" + String.format("%03d", currentTicket.getTicketNumber()));
        tvServiceName.setText(currentTicket.getServiceName());
        tvPeopleAhead.setText(currentTicket.getPeopleAhead() + " người");
        tvEstimatedTime.setText(currentTicket.getEstimatedTime());

        // Cập nhật trạng thái (Status) với màu sắc (Color)
        String status = currentTicket.getStatus();
        switch (status) {
            case "waiting":
                tvStatus.setText("Đang chờ");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "called":
                tvStatus.setText("Đã gọi số");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "serving":
                tvStatus.setText("Đang phục vụ");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "completed":
                tvStatus.setText("Hoàn thành");
                tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    private void startRealtimeUpdates() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Mock: Giảm (Decrease) số người chờ mỗi 5 giây
                if (currentTicket.getPeopleAhead() > 0) {
                    currentTicket.setPeopleAhead(currentTicket.getPeopleAhead() - 1);

                    // Cập nhật thời gian dự kiến (Estimated Time)
                    int minutes = currentTicket.getPeopleAhead() * 3;
                    currentTicket.setEstimatedTime(minutes + " phút");

                    // Nếu đến lượt (Turn)
                    if (currentTicket.getPeopleAhead() == 0) {
                        currentTicket.setStatus("called");
                    }

                    updateUI();
                }

                // Lặp lại (Repeat) sau 5 giây
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng (Stop) updates khi đóng (Close) màn hình
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}