package com.pascs.citizen; // (Gói GỐC)

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.activities.MainActivity;
// import com.pascs.citizen.models.Appointment; // (Sẽ dùng khi có API)

import java.util.Calendar;

public class BookAppointmentActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvSelectedDate, tvSelectedTime;
    private EditText etNotes;
    private Button btnSelectDate, btnSelectTime, btnConfirmAppointment;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarBookAppointment);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        etNotes = findViewById(R.id.etNotes);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmAppointment = findViewById(R.id.btnConfirmAppointment);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đặt lịch hẹn");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút Back
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Xử lý (Handle) sự kiện (Events) ---
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnConfirmAppointment.setOnClickListener(v -> handleConfirmAppointment());
    }

    // (Hàm này để xử lý khi nhấn nút Back trên Toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng (Close) Activity này và quay lại MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvSelectedDate.setText(selectedDate);
                },
                year, month, day
        );

        // Chỉ cho phép chọn từ ngày mai trở đi
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = 8; // Mặc định (Default) 8h sáng
        int minute = 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Chỉ cho phép chọn giờ làm việc (Working Hours) 8h-17h
                    if (selectedHour < 8 || selectedHour > 17) {
                        Toast.makeText(this,
                                "Vui lòng chọn giờ làm việc (8:00 - 17:00)",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    tvSelectedTime.setText(selectedTime);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void handleConfirmAppointment() {
        // Kiểm tra (Validation)
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hẹn", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giờ hẹn", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = etNotes.getText().toString().trim();

        // (Hiện tại, chúng ta chỉ hiển thị thông báo TEST)
        btnConfirmAppointment.setEnabled(false);
        btnConfirmAppointment.setText("Đang xử lý...");

        Toast.makeText(this,
                "[TEST] Đã gửi yêu cầu đặt lịch\n" +
                        "Ngày: " + selectedDate + "\n" +
                        "Giờ: " + selectedTime,
                Toast.LENGTH_LONG).show();

        // (Sau 1.5 giây thì giả lập (Simulate) là thành công)
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this,
                    "[TEST] Đặt lịch thành công!",
                    Toast.LENGTH_SHORT).show();

            // (SAU NÀY: Có thể chuyển sang màn hình "Lịch hẹn của tôi")
            // Intent intent = new Intent(BookAppointmentActivity.this, AppointmentListActivity.class);
            // startActivity(intent);

            finish(); // Đóng màn hình này
        }, 1500);
    }
}