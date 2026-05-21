package com.unirent.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.activities.CompareActivity;
import com.unirent.app.adapters.RoomAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import com.unirent.app.utils.SessionManager;
import java.util.*;

public class FavoriteFragment extends Fragment {
    private final Set<String> picked = new LinkedHashSet<>();
    private List<Room> data;
    private RoomAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_favorite, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        String uid = new SessionManager(requireContext()).getUserId();
        if (uid == null) return root;

        List<String> ids = db.favoriteDao().getRoomIds(uid);
        data = new ArrayList<>();
        for (String id : ids) {
            Room r = db.roomDao().getById(id);
            if (r != null) data.add(r);
        }
        TextView empty = root.findViewById(R.id.tv_empty);
        empty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);

        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter();
        adapter.setOnLongClick(r -> togglePick(r));
        adapter.setData(data);
        rv.setAdapter(adapter);

        Button btn = root.findViewById(R.id.btn_compare);
        btn.setOnClickListener(v -> {
            if (picked.size() < 2) {
                Toast.makeText(getContext(), "Giữ lâu để chọn ít nhất 2 phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(getContext(), CompareActivity.class);
            i.putStringArrayListExtra(CompareActivity.EXTRA_ROOM_IDS, new ArrayList<>(picked));
            startActivity(i);
        });

        return root;
    }

    private void togglePick(Room r) {
        if (picked.contains(r.roomId)) picked.remove(r.roomId);
        else picked.add(r.roomId);
        Toast.makeText(getContext(),
            "Đã chọn " + picked.size() + " phòng để so sánh", Toast.LENGTH_SHORT).show();
    }
}
