package com.pascs.citizen; // (Gói GỐC)

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.models.Appointment;

public class AppointmentDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvAppointmentId, tvServiceType, tvDate, tvTime, tvStatus, tvNotes;
    private Button btnCancelAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarAppointmentDetail);
        tvAppointmentId = findViewById(R.id.tvAppointmentId);
        tvServiceType = findViewById(R.id.tvServiceType);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvNotes = findViewById(R.id.tvNotes);
        btnCancelAppointment = findViewById(R.id.btnCancelAppointment);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết lịch hẹn");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Lấy (Get) appointment ID từ (from) intent
        int appointmentId = getIntent().getIntExtra("APPOINTMENT_ID", -1);

        // --- Tải (Load) chi tiết (Detail) ---
        loadAppointmentDetail(appointmentId);

        // --- Xử lý (Handle) Button Hủy (Cancel) ---
        btnCancelAppointment.setOnClickListener(v -> showCancelDialog());
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

    private void loadAppointmentDetail(int id) {
        // Mock data
        Appointment appointment = new Appointment(id, "Gặp cán bộ",
                "25/11/2024", "09:00", "confirmed");
        appointment.setNotes("Vui lòng đến đúng giờ. Cán bộ sẽ tư vấn về thủ tục hành chính.");

        // Hiển thị (Display) data
        tvAppointmentId.setText("#" + appointment.getId());
        tvServiceType.setText(appointment.getServiceType());
        tvDate.setText(appointment.getAppointmentDate());
        tvTime.setText(appointment.getAppointmentTime());
        tvNotes.setText(appointment.getNotes());

        // Cài đặt (Set) trạng thái (Status) với màu sắc (Color)
        String status = appointment.getStatus();
        switch (status) {
            case "pending":
                tvStatus.setText("Chờ xác nhận");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                btnCancelAppointment.setEnabled(true);
                break;
            case "confirmed":
                tvStatus.setText("Đã xác nhận");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                btnCancelAppointment.setEnabled(true);
                break;
            case "cancelled":
                tvStatus.setText("Đã hủy");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnCancelAppointment.setEnabled(false);
                btnCancelAppointment.setText("Đã hủy lịch");
                break;
            case "completed":
                tvStatus.setText("Hoàn thành");
                tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                btnCancelAppointment.setEnabled(false);
                btnCancelAppointment.setText("Đã hoàn thành");
                break;
        }
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc muốn hủy lịch hẹn này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Mock: Hủy (Cancel) lịch hẹn (Appointment)
                    Toast.makeText(this, "[TEST] Đã hủy lịch hẹn!", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Đã hủy");
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnCancelAppointment.setEnabled(false);
                    btnCancelAppointment.setText("Đã hủy lịch");
                })
                .setNegativeButton("Không", null)
                .show();
    }
}