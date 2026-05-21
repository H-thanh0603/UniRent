package com.unirent.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.unirent.app.R;
import com.unirent.app.activities.AddRoomActivity;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;
import com.unirent.app.utils.SessionManager;

import java.util.List;

public class LandlordDashboardFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_landlord_dashboard, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        User me = new SessionManager(requireContext()).getCurrentUser();
        if (me == null) return root;

        ((TextView)root.findViewById(R.id.tv_hello)).setText("Xin chào, " + me.fullName);

        List<Room> rooms = db.roomDao().getByLandlord(me.userId);
        int total = rooms.size(), available = 0, views = 0, favs = 0;
        for (Room r : rooms) {
            if (Room.STATUS_AVAILABLE.equals(r.status) && Room.APPROVED.equals(r.approvedStatus)) available++;
            views += r.viewCount;
            favs += r.favoriteCount;
        }
        ((TextView)root.findViewById(R.id.tv_total)).setText(String.valueOf(total));
        ((TextView)root.findViewById(R.id.tv_available)).setText(String.valueOf(available));
        ((TextView)root.findViewById(R.id.tv_views)).setText(String.valueOf(views));
        ((TextView)root.findViewById(R.id.tv_favs)).setText(String.valueOf(favs));

        int appts = db.appointmentDao().getByLandlord(me.userId).size();
        ((TextView)root.findViewById(R.id.tv_appts)).setText(String.valueOf(appts));

        root.findViewById(R.id.btn_add_room).setOnClickListener(v ->
            startActivity(new Intent(getContext(), AddRoomActivity.class)));
        return root;
    }
}
