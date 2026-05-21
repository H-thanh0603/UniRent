package com.unirent.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.activities.AddRoomActivity;
import com.unirent.app.adapters.RoomAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.utils.SessionManager;

public class LandlordRoomsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_landlord_rooms, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        String uid = new SessionManager(requireContext()).getUserId();

        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        RoomAdapter adapter = new RoomAdapter();
        adapter.setOnLongClick(r -> {
            Intent i = new Intent(getContext(), AddRoomActivity.class);
            i.putExtra(AddRoomActivity.EXTRA_ROOM_ID, r.roomId);
            startActivity(i);
        });
        adapter.setData(db.roomDao().getByLandlord(uid));
        rv.setAdapter(adapter);

        root.findViewById(R.id.btn_add).setOnClickListener(v ->
            startActivity(new Intent(getContext(), AddRoomActivity.class)));
        return root;
    }

    @Override public void onResume() {
        super.onResume();
        // Refresh on return
        if (getView() != null) {
            RecyclerView rv = getView().findViewById(R.id.rv);
            if (rv != null && rv.getAdapter() instanceof RoomAdapter) {
                String uid = new SessionManager(requireContext()).getUserId();
                ((RoomAdapter)rv.getAdapter()).setData(
                    AppDatabase.getInstance(requireContext()).roomDao().getByLandlord(uid));
            }
        }
    }
}
