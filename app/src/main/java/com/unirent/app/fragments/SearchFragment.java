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
    private static String pendingArea = null;
    public static void setPendingArea(String area) { pendingArea = area; }

    private RoomAdapter adapter;
    private List<Room> all;
    private long maxPrice = Long.MAX_VALUE;
    private double maxDist = Double.MAX_VALUE;
    private TextView tvAiHint, tvAreaSuggestion;
    private EditText etQ;
    private ProgressBar progress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_search, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        all = db.roomDao().getAllApproved();

        etQ = root.findViewById(R.id.et_query);
        tvAiHint = root.findViewById(R.id.tv_ai_hint);
        progress = root.findViewById(R.id.progress);
        RecyclerView rv = root.findViewById(R.id.rv_results);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter();
        rv.setAdapter(adapter);

        applyFilter("");

        // Nếu có pending area từ AreaAdvisor → auto search
        if (pendingArea != null) {
            etQ.setText(pendingArea);
            aiSearch("Phòng trọ khu vực " + pendingArea);
            pendingArea = null;
        }

        // 🤖 AI tìm giúp
        root.findViewById(R.id.btn_ai_filter).setOnClickListener(v -> {
            String q = etQ.getText().toString().trim();
            if (q.isEmpty()) { Toast.makeText(getContext(), "Nhập yêu cầu của bạn trước nhé!", Toast.LENGTH_SHORT).show(); return; }
            aiSearch(q);
        });

        // Live filter
        etQ.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
            public void onTextChanged(CharSequence c, int a, int b, int d) { applyFilter(c.toString()); }
            public void afterTextChanged(Editable e) {}
        });

        // Quick chips
        bindChip(root, R.id.chip_under_2m,  () -> { maxPrice = 2_000_000; maxDist = Double.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_2_3m,     () -> { maxPrice = 3_000_000; maxDist = Double.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_above_3m, () -> { maxPrice = Long.MAX_VALUE; maxDist = Double.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_near_2km, () -> { maxDist = 2.0; maxPrice = Long.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_near_5km, () -> { maxDist = 5.0; maxPrice = Long.MAX_VALUE; applyFilter(etQ.getText().toString()); });
        bindChip(root, R.id.chip_clear,    () -> { maxPrice = Long.MAX_VALUE; maxDist = Double.MAX_VALUE; etQ.setText(""); applyFilter(""); });

        return root;
    }

    private void aiSearch(String query) {
        progress.setVisibility(View.VISIBLE);
        tvAiHint.setText("🤖 AI đang phân tích yêu cầu của bạn...");
        tvAiHint.setVisibility(View.VISIBLE);

        AiHelper.agentFilter(query, all, rooms -> {
            progress.setVisibility(View.GONE);
            if (rooms.isEmpty()) {
                tvAiHint.setText("😕 Không tìm thấy phòng phù hợp. Thử yêu cầu khác nhé!");
                adapter.setData(new ArrayList<>());
            } else {
                tvAiHint.setText("✅ AI tìm được " + rooms.size() + " phòng phù hợp nhất cho bạn:");
                adapter.setData(rooms);
            }
        });
    }

    private void bindChip(View root, int id, Runnable r) {
        View v = root.findViewById(id); if (v != null) v.setOnClickListener(x -> r.run());
    }

    private void applyFilter(String q) {
        String low = q == null ? "" : q.toLowerCase(Locale.ROOT).trim();
        List<Room> result = new ArrayList<>();
        for (Room r : all) {
            if (r.price > maxPrice) continue;
            if (r.distanceToSchool > maxDist) continue;
            if (!low.isEmpty()) {
                String hay = (r.title + " " + r.address + " " + r.schoolName + " "
                    + (r.amenities != null ? String.join(" ", r.amenities) : "")).toLowerCase(Locale.ROOT);
                String[] words = low.split("\\s+");
                boolean match = true;
                for (String w : words) { if (!hay.contains(w)) { match = false; break; } }
                if (!match) continue;
            }
            result.add(r);
        }
        adapter.setData(result);
        if (tvAiHint != null && progress.getVisibility() != View.VISIBLE) {
            tvAiHint.setText(result.isEmpty() ? "🔍 " + all.size() + " phòng — thử AI tìm giúp?" : "📋 " + result.size() + " phòng");
        }
    }
}
