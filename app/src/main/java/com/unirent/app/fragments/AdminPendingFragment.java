package com.unirent.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.adapters.RoomAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;

import java.util.List;

public class AdminPendingFragment extends Fragment {
    private RoomAdapter adapter;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_admin_pending, parent, false);
        db = AppDatabase.getInstance(requireContext());

        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter();
        adapter.setOnLongClick(r -> showDialog(r));
        rv.setAdapter(adapter);
        reload();
        TextView tv = root.findViewById(R.id.tv_hint);
        tv.setText("Giữ lâu một item để Duyệt / Từ chối tin đăng.");
        return root;
    }

    private void showDialog(Room r) {
        String[] options = {"✓ Duyệt tin", "✗ Từ chối"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(r.title)
            .setItems(options, (d, w) -> {
                r.approvedStatus = w == 0 ? Room.APPROVED : Room.REJECTED;
                r.updatedAt = System.currentTimeMillis();
                db.roomDao().update(r);
                Toast.makeText(getContext(), w == 0 ? "Đã duyệt" : "Đã từ chối", Toast.LENGTH_SHORT).show();
                reload();
            }).show();
    }

    private void reload() {
        List<Room> list = db.roomDao().getPending();
        adapter.setData(list);
    }
}
