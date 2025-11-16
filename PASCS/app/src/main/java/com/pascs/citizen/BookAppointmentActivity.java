package com.pascs.citizen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.R;

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

        // Khởi tạo Views
        toolbar = findViewById(R.id.toolbarBookAppointment);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        etNotes = findViewById(R.id.etNotes);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmAppointment = findViewById(R.id.btnConfirmAppointment);

        // --- Cài đặt Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // ✅ SỬA: Dùng getString()
            getSupportActionBar().setTitle(getString(R.string.title_book_appointment));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Xử lý sự kiện ---
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnConfirmAppointment.setOnClickListener(v -> handleConfirmAppointment());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
        int hour = 8; // Mặc định 8h sáng
        int minute = 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Chỉ cho phép chọn giờ làm việc 8h-17h
                    if (selectedHour < 8 || selectedHour > 17) {
                        // ✅ SỬA: Dùng getString()
                        Toast.makeText(this,
                                getString(R.string.working_hours_only),
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
        // Kiểm tra Validation
        if (selectedDate.isEmpty()) {
            // ✅ SỬA: Dùng getString()
            Toast.makeText(this, getString(R.string.select_date_prompt), Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime.isEmpty()) {
            // ✅ SỬA: Dùng getString()
            Toast.makeText(this, getString(R.string.select_time_prompt), Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = etNotes.getText().toString().trim();

        btnConfirmAppointment.setEnabled(false);
        // ✅ SỬA: Dùng getString()
        btnConfirmAppointment.setText(getString(R.string.processing));

        // ✅ SỬA: Dùng String.format
        Toast.makeText(this,
                String.format(getString(R.string.test_appointment_success),
                        selectedDate, selectedTime),
                Toast.LENGTH_LONG).show();

        // Sau 1.5 giây thì giả lập là thành công
        new android.os.Handler().postDelayed(() -> {
            finish();
        }, 1500);
    }
}