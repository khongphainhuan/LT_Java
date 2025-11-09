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
import com.pascs.citizen.AppointmentDetailActivity; // ✅ Package gốc
import com.pascs.citizen.models.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.tvAppointmentId.setText("#" + appointment.getId());
        holder.tvServiceType.setText(appointment.getServiceType()); // "Gặp cán bộ"
        holder.tvDateTime.setText("📅 " + appointment.getAppointmentDate() + " - 🕐 " + appointment.getAppointmentTime());

        // Cài đặt (Set) trạng thái (Status) với màu sắc (Color) khác nhau
        String status = appointment.getStatus();
        switch (status) {
            case "pending":
                holder.tvStatus.setText("Chờ xác nhận");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "confirmed":
                holder.tvStatus.setText("Đã xác nhận");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "cancelled":
                holder.tvStatus.setText("Đã hủy");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "completed":
                holder.tvStatus.setText("Hoàn thành");
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        // Click vào item → Mở (Open) chi tiết (Detail)
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppointmentDetailActivity.class);
            intent.putExtra("APPOINTMENT_ID", appointment.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvAppointmentId, tvServiceType, tvDateTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardAppointment);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentId);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}