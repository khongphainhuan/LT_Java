package com.pascs.citizen; // (Gói GỐC)

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

import java.util.ArrayList;
import java.util.List;

public class ApplicationListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ApplicationAdapter adapter;

    // (Danh sách (List) hồ sơ (Applications))
    private List<Application> applicationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarApplicationList);
        recyclerView = findViewById(R.id.recyclerViewApplications);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Theo dõi hồ sơ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Cài đặt (Setup) RecyclerView ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- Tải (Load) dữ liệu (Data) giả (Mock) ---
        loadMockApplications();
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

    private void loadMockApplications() {
        progressBar.setVisibility(View.VISIBLE);

        // Giả lập (Simulate) loading
        new android.os.Handler().postDelayed(() -> {
            // Mock data
            applicationList.add(new Application(1, "Cấp Căn cước công dân", "processing", "15/10/2024"));
            applicationList.add(new Application(2, "Đăng ký Giấy phép lái xe", "completed", "10/10/2024"));
            applicationList.add(new Application(3, "Đăng ký tạm trú", "need_supplement", "20/10/2024"));
            applicationList.add(new Application(4, "Đăng ký khai sinh", "submitted", "25/10/2024"));

            progressBar.setVisibility(View.GONE);

            if (applicationList.isEmpty()) {
                // Không có dữ liệu (No Data)
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Có dữ liệu (Has Data)
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter = new ApplicationAdapter(this, applicationList);
                recyclerView.setAdapter(adapter);
            }
        }, 1000);
    }
}