package com.pascs.citizen;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.adapters.ApplicationAdapter;
import com.pascs.citizen.models.Application;
import com.pascs.citizen.R;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ApplicationAdapter adapter;

    // Danh sách hồ sơ
    private List<Application> applicationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        // Khởi tạo Views
        toolbar = findViewById(R.id.toolbarApplicationList);
        recyclerView = findViewById(R.id.recyclerViewApplications);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // --- Cài đặt Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // ✅ SỬA: Dùng getString()
            getSupportActionBar().setTitle(getString(R.string.title_track_application));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Cài đặt RecyclerView ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- Tải dữ liệu giả ---
        loadMockApplications();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMockApplications() {
        progressBar.setVisibility(View.VISIBLE);

        // Giả lập loading
        new android.os.Handler().postDelayed(() -> {
            // ✅ SỬA: Mock data với getString()
            applicationList.add(new Application(
                    1,
                    getString(R.string.service_cccd),
                    "processing",
                    "15/10/2024"
            ));
            applicationList.add(new Application(
                    2,
                    getString(R.string.service_driving_license),
                    "completed",
                    "10/10/2024"
            ));
            applicationList.add(new Application(
                    3,
                    getString(R.string.service_residence),
                    "need_supplement",
                    "20/10/2024"
            ));
            // ✅ SỬA: Thêm string mới cho "Đăng ký khai sinh"
            applicationList.add(new Application(
                    4,
                    "Birth Certificate Registration", // Mock - có thể thêm vào strings.xml
                    "submitted",
                    "25/10/2024"
            ));

            progressBar.setVisibility(View.GONE);

            if (applicationList.isEmpty()) {
                // Không có dữ liệu
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Có dữ liệu
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter = new ApplicationAdapter(this, applicationList);
                recyclerView.setAdapter(adapter);
            }
        }, 1000);
    }
}