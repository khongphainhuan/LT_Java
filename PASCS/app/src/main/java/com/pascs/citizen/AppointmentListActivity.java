package com.pascs.citizen; // (Gói GỐC)

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pascs.citizen.adapters.AppointmentAdapter;
import com.pascs.citizen.models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private AppointmentAdapter adapter;

    // (Danh sách (List) lịch hẹn (Appointments))
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarAppointmentList);
        recyclerView = findViewById(R.id.recyclerViewAppointments);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch hẹn của tôi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Cài đặt (Setup) RecyclerView ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- Tải (Load) dữ liệu (Data) giả (Mock) ---
        loadMockAppointments();
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

    private void loadMockAppointments() {
        progressBar.setVisibility(View.VISIBLE);

        // Giả lập (Simulate) loading
        new android.os.Handler().postDelayed(() -> {
            // Mock data
            appointmentList.add(new Appointment(1, "Gặp cán bộ", "25/11/2024", "09:00", "confirmed"));
            appointmentList.add(new Appointment(2, "Gặp cán bộ", "26/11/2024", "14:30", "pending"));
            appointmentList.add(new Appointment(3, "Gặp cán bộ", "20/11/2024", "10:00", "completed"));
            appointmentList.add(new Appointment(4, "Gặp cán bộ", "18/11/2024", "15:00", "cancelled"));

            progressBar.setVisibility(View.GONE);

            if (appointmentList.isEmpty()) {
                // Không có dữ liệu (No Data)
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Có dữ liệu (Has Data)
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter = new AppointmentAdapter(this, appointmentList);
                recyclerView.setAdapter(adapter);
            }
        }, 1000);
    }
}