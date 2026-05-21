package com.unirent.app.fragments;

import android.os.Bundle;
import android.text.*;
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
import java.util.*;

public class SearchFragment extends Fragment {
    private RoomAdapter adapter;
    private List<Room> all;
    private long maxPrice = Long.MAX_VALUE;
    private double maxDist = Double.MAX_VALUE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_search, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        all = db.roomDao().getAllApproved();

        EditText etQ = root.findViewById(R.id.et_query);
        RecyclerView rv = root.findViewById(R.id.rv_results);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter();
        rv.setAdapter(adapter);

        applyFilter("");

        etQ.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
            public void onTextChanged(CharSequence c, int a, int b, int d) { applyFilter(c.toString()); }
            public void afterTextChanged(Editable e) {}
        });

        // Quick filter chips
        bindChip(root, R.id.chip_under_2m, () -> { maxPrice = 2_000_000; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_2_3m,    () -> { maxPrice = 3_000_000; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_above_3m,() -> { maxPrice = Long.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_near_2km, () -> { maxDist = 2.0; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_near_5km, () -> { maxDist = 5.0; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_clear, () -> {
            maxPrice = Long.MAX_VALUE; maxDist = Double.MAX_VALUE;
            etQ.setText(""); applyFilter("");
        });

        return root;
    }

    private void bindChip(View root, int id, Runnable r) {
        View v = root.findViewById(id);
        if (v != null) v.setOnClickListener(x -> r.run());
    }

    private void applyFilter(String q) {
        String low = q == null ? "" : q.toLowerCase(Locale.ROOT).trim();
        List<Room> result = new ArrayList<>();
        for (Room r : all) {
            if (r.price > maxPrice) continue;
            if (r.distanceToSchool > maxDist) continue;
            if (!low.isEmpty()) {
                String hay = (r.title + " " + r.address + " " + r.schoolName).toLowerCase(Locale.ROOT);
                if (!hay.contains(low)) continue;
            }
            result.add(r);
        }
        adapter.setData(result);
    }
}
