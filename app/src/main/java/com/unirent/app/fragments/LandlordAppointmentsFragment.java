package com.unirent.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.adapters.AppointmentAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Appointment;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

public class LandlordAppointmentsFragment extends Fragment {
    private AppointmentAdapter adapter;
    private AppDatabase db;
    private User me;
    private boolean asLandlord;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_appointments, parent, false);
        db = AppDatabase.getInstance(requireContext());
        me = new SessionManager(requireContext()).getCurrentUser();
        asLandlord = me != null && User.ROLE_LANDLORD.equals(me.role);

        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentAdapter(db, asLandlord, (a, action) -> {
            a.status = action;
            a.updatedAt = System.currentTimeMillis();
            db.appointmentDao().update(a);
            Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
            reload();
        });
        rv.setAdapter(adapter);
        reload();
        return root;
    }

    private void reload() {
        if (me == null) return;
        adapter.setData(asLandlord ? db.appointmentDao().getByLandlord(me.userId)
                                    : db.appointmentDao().getByStudent(me.userId));
    }
}
