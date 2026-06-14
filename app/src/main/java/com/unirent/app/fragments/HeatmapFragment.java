package com.unirent.app.fragments;

import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import java.util.*;

/** Heatmap đơn giản: hiển thị các khu vực với màu theo giá trung bình */
public class HeatmapFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(requireContext());
        title.setText("🗺️ Bản đồ nhiệt giá thuê");
        title.setTextColor(getResources().getColor(R.color.text_primary));
        title.setTextSize(20); title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 8, 0, 16);
        root.addView(title);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<Room> rooms = db.roomDao().getAllApproved();

        // Nhóm theo school
        Map<String, List<Room>> bySchool = new LinkedHashMap<>();
        for (Room r : rooms) {
            bySchool.computeIfAbsent(r.schoolName != null ? r.schoolName : "Khác", k -> new ArrayList<>()).add(r);
        }

        for (Map.Entry<String, List<Room>> entry : bySchool.entrySet()) {
            List<Room> list = entry.getValue();
            long avgPrice = (long) list.stream().mapToLong(r -> r.price).average().orElse(0);

            // Color: green (cheap) -> yellow -> red (expensive)
            int color;
            if (avgPrice < 1500000) color = 0xFF4CAF50;
            else if (avgPrice < 2200000) color = 0xFFFFC107;
            else if (avgPrice < 3000000) color = 0xFFFF9800;
            else color = 0xFFF44336;

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            View dot = new View(requireContext());
            dot.setLayoutParams(new LinearLayout.LayoutParams(24, 24));
            dot.setBackgroundColor(color);
            // circular shape
            dot.post(() -> {
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                gd.setColor(color);
                dot.setBackground(gd);
            });

            TextView tv = new TextView(requireContext());
            tv.setText(String.format(Locale.US, "  %s: ~%,dđ/tháng (%d phòng)", entry.getKey(), avgPrice, list.size()));
            tv.setTextColor(getResources().getColor(R.color.text_primary));
            tv.setTextSize(14);
            tv.setPadding(8, 0, 0, 0);

            row.addView(dot);
            row.addView(tv);
            root.addView(row);
        }

        ScrollView sv = new ScrollView(requireContext());
        sv.addView(root);
        FrameLayout fl = new FrameLayout(requireContext());
        fl.addView(sv);
        return fl;
    }
}
