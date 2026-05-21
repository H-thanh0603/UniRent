package com.unirent.app.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.VH> {
    public interface ActionListener { void onAction(Appointment a, String action); }
    private final List<Appointment> data = new ArrayList<>();
    private final boolean asLandlord;
    private final ActionListener listener;
    private final AppDatabase db;

    public AppointmentAdapter(AppDatabase db, boolean asLandlord, ActionListener l) {
        this.db = db; this.asLandlord = asLandlord; this.listener = l;
    }

    public void setData(List<Appointment> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_appointment, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Appointment a = data.get(pos);
        Room r = db.roomDao().getById(a.roomId);
        h.tvTitle.setText(r != null ? r.title : "(phòng đã xóa)");
        h.tvTime.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date(a.appointmentTime)));
        User other = db.userDao().getById(asLandlord ? a.studentId : a.landlordId);
        h.tvWith.setText((asLandlord ? "Sinh viên: " : "Chủ trọ: ") + (other != null ? other.fullName : "?"));
        h.tvNote.setText(a.note == null || a.note.isEmpty() ? "Không có ghi chú" : a.note);
        h.tvStatus.setText(label(a.status));
        styleBadge(h.tvStatus, a.status);

        if (asLandlord && Appointment.PENDING.equals(a.status)) {
            h.row.setVisibility(View.VISIBLE);
            h.btnAccept.setOnClickListener(v -> listener.onAction(a, Appointment.ACCEPTED));
            h.btnReject.setOnClickListener(v -> listener.onAction(a, Appointment.REJECTED));
        } else if (!asLandlord && Appointment.PENDING.equals(a.status)) {
            h.row.setVisibility(View.VISIBLE);
            h.btnAccept.setVisibility(View.GONE);
            h.btnReject.setText("Hủy lịch");
            h.btnReject.setOnClickListener(v -> listener.onAction(a, Appointment.CANCELLED));
        } else {
            h.row.setVisibility(View.GONE);
        }
    }

    private String label(String s) {
        switch (s) {
            case Appointment.PENDING: return "Chờ xác nhận";
            case Appointment.ACCEPTED: return "Đã xác nhận";
            case Appointment.REJECTED: return "Bị từ chối";
            case Appointment.CANCELLED: return "Đã hủy";
            case Appointment.COMPLETED: return "Hoàn thành";
            default: return s;
        }
    }

    private void styleBadge(TextView v, String s) {
        int bg, txt;
        switch (s) {
            case Appointment.ACCEPTED: case Appointment.COMPLETED:
                bg = R.drawable.bg_chip_green; txt = R.color.badge_green_text; break;
            case Appointment.REJECTED: case Appointment.CANCELLED:
                bg = R.drawable.bg_chip_red; txt = R.color.badge_red_text; break;
            default: bg = R.drawable.bg_chip_blue; txt = R.color.badge_blue_text;
        }
        v.setBackgroundResource(bg);
        v.setTextColor(v.getContext().getResources().getColor(txt));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvWith, tvNote, tvStatus;
        Button btnAccept, btnReject;
        View row;
        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_title);
            tvTime = v.findViewById(R.id.tv_time);
            tvWith = v.findViewById(R.id.tv_with);
            tvNote = v.findViewById(R.id.tv_note);
            tvStatus = v.findViewById(R.id.tv_status);
            btnAccept = v.findViewById(R.id.btn_accept);
            btnReject = v.findViewById(R.id.btn_reject);
            row = v.findViewById(R.id.row_actions);
        }
    }
}
