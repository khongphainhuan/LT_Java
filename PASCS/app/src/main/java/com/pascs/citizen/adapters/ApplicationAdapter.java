package com.pascs.citizen.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pascs.citizen.R;
import com.pascs.citizen.ApplicationDetailActivity; // ✅ Package gốc
import com.pascs.citizen.models.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private Context context;
    private List<Application> applicationList;

    public ApplicationAdapter(Context context, List<Application> applicationList) {
        this.context = context;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applicationList.get(position);

        holder.tvApplicationId.setText("#" + app.getId());
        holder.tvServiceType.setText(app.getServiceType());
        holder.tvSubmitDate.setText("Ngày gửi: " + app.getSubmitDate());

        // Cài đặt (Set) trạng thái (Status) với màu sắc (Color) khác nhau
        String status = app.getStatus();
        switch (status) {
            case "submitted":
                holder.tvStatus.setText("Đã gửi");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "processing":
                holder.tvStatus.setText("Đang xử lý");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "need_supplement":
                holder.tvStatus.setText("Cần bổ sung");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "completed":
                holder.tvStatus.setText("Hoàn thành");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
        }

        // Click vào item → Mở (Open) chi tiết (Detail)
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplicationDetailActivity.class);
            intent.putExtra("APPLICATION_ID", app.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvApplicationId, tvServiceType, tvSubmitDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardApplication);
            tvApplicationId = itemView.findViewById(R.id.tvApplicationId);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvSubmitDate = itemView.findViewById(R.id.tvSubmitDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}