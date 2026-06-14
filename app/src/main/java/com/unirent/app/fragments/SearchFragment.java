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
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import java.util.*;

public class SearchFragment extends Fragment {
    private RoomAdapter adapter;
    private List<Room> all;
    private long maxPrice = Long.MAX_VALUE;
    private double maxDist = Double.MAX_VALUE;
    private TextView tvAiHint;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_search, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        all = db.roomDao().getAllApproved();

        EditText etQ = root.findViewById(R.id.et_query);
        tvAiHint = root.findViewById(R.id.tv_ai_hint);
        RecyclerView rv = root.findViewById(R.id.rv_results);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter();
        rv.setAdapter(adapter);

        applyFilter("");

        // Text search + AI khi bấm nút search
        etQ.setOnEditorActionListener((v, actionId, event) -> {
            String q = etQ.getText().toString().trim();
            if (!q.isEmpty()) {
                applyFilter(q);
                // AI phân tích yêu cầu
                AiHelper.parseSearch(q, summary ->
                    tvAiHint.post(() -> tvAiHint.setText(summary)));
            }
            return true;
        });

        // Live filter khi gõ
        etQ.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
            public void onTextChanged(CharSequence c, int a, int b, int d) { applyFilter(c.toString()); }
            public void afterTextChanged(Editable e) {}
        });

        // Quick filter chips
        bindChip(root, R.id.chip_under_2m,  () -> { maxPrice = 2_000_000; maxDist = Double.MAX_VALUE; refresh(etQ); });
        bindChip(root, R.id.chip_2_3m,     () -> { maxPrice = 3_000_000; maxDist = Double.MAX_VALUE; refresh(etQ); });
        bindChip(root, R.id.chip_above_3m, () -> { maxPrice = Long.MAX_VALUE; maxDist = Double.MAX_VALUE; refresh(etQ); });
        bindChip(root, R.id.chip_near_2km, () -> { maxDist = 2.0; maxPrice = Long.MAX_VALUE; refresh(etQ); });
        bindChip(root, R.id.chip_near_5km, () -> { maxDist = 5.0; maxPrice = Long.MAX_VALUE; refresh(etQ); });
        bindChip(root, R.id.chip_clear,    () -> { maxPrice = Long.MAX_VALUE; maxDist = Double.MAX_VALUE; etQ.setText(""); applyFilter(""); });

        return root;
    }

    private void refresh(EditText etQ) { applyFilter(etQ.getText().toString()); }

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
                String hay = (r.title + " " + r.address + " " + r.schoolName + " "
                    + (r.amenities != null ? String.join(" ", r.amenities) : ""))
                    .toLowerCase(Locale.ROOT);
                // Match từng từ trong query
                String[] words = low.split("\\s+");
                boolean match = true;
                for (String w : words) {
                    if (!hay.contains(w)) { match = false; break; }
                }
                if (!match) continue;
            }
            result.add(r);
        }
        adapter.setData(result);
        if (tvAiHint != null) {
            tvAiHint.setText(result.isEmpty() ? "🔍 Không tìm thấy phòng phù hợp. Thử từ khóa khác nhé!"
                : "📋 " + result.size() + " phòng phù hợp");
        }
    }
}
