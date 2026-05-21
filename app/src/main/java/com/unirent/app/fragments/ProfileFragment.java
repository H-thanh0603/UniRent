package com.unirent.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.unirent.app.R;
import com.unirent.app.activities.LoginActivity;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_profile, parent, false);
        SessionManager sm = new SessionManager(requireContext());
        User u = sm.getCurrentUser();
        if (u == null) return root;

        ((TextView)root.findViewById(R.id.tv_name)).setText(u.fullName);
        ((TextView)root.findViewById(R.id.tv_email)).setText(u.email);
        ((TextView)root.findViewById(R.id.tv_phone)).setText(u.phone == null || u.phone.isEmpty() ? "(chưa cập nhật)" : u.phone);
        ((TextView)root.findViewById(R.id.tv_role)).setText(label(u.role));
        ((TextView)root.findViewById(R.id.tv_school)).setText(u.schoolName == null || u.schoolName.isEmpty() ? "(chưa cập nhật)" : u.schoolName);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        if (User.ROLE_STUDENT.equals(u.role)) {
            int favs = db.favoriteDao().getRoomIds(u.userId).size();
            int appts = db.appointmentDao().getByStudent(u.userId).size();
            ((TextView)root.findViewById(R.id.tv_stats)).setText("Phòng đã lưu: " + favs + " · Lịch hẹn: " + appts);
        } else if (User.ROLE_LANDLORD.equals(u.role)) {
            int rooms = db.roomDao().getByLandlord(u.userId).size();
            int appts = db.appointmentDao().getByLandlord(u.userId).size();
            ((TextView)root.findViewById(R.id.tv_stats)).setText("Phòng đăng: " + rooms + " · Yêu cầu xem: " + appts);
        }

        root.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            sm.logout();
            Intent i = new Intent(getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        });
        return root;
    }

    private String label(String role) {
        switch (role) {
            case User.ROLE_STUDENT: return "Sinh viên";
            case User.ROLE_LANDLORD: return "Chủ trọ";
            case User.ROLE_ADMIN: return "Quản trị viên";
            default: return role;
        }
    }
}
