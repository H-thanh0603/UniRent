package com.unirent.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.adapters.RoommateAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Roommate;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;
import java.util.*;

public class RoommateFragment extends Fragment {
    private RoommateAdapter adapter;
    private List<Roommate> all;
    private String filterGender = "Tất cả";
    private String filterLifestyle = "Tất cả";
    private User me;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_roommate, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        me = new SessionManager(requireContext()).getCurrentUser();
        all = db.roommateDao().getAllExcept(me != null ? me.userId : "");

        RecyclerView rv = root.findViewById(R.id.rv_roommates);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoommateAdapter();
        rv.setAdapter(adapter);

        applyFilter();

        // Filter chips
        addFilterChip(root, "Tất cả", () -> { filterGender = "Tất cả"; filterLifestyle = "Tất cả"; applyFilter(); });
        addFilterChip(root, "Nam", () -> { filterGender = "Nam"; applyFilter(); });
        addFilterChip(root, "Nữ", () -> { filterGender = "Nữ"; applyFilter(); });
        addFilterChip(root, "Sáng tạo", () -> { filterLifestyle = "Sáng tạo"; applyFilter(); });
        addFilterChip(root, "Năng động", () -> { filterLifestyle = "Năng động"; applyFilter(); });
        addFilterChip(root, "Yên tĩnh", () -> { filterLifestyle = "Yên tĩnh"; applyFilter(); });

        return root;
    }

    private void addFilterChip(View root, String label, Runnable r) {
        LinearLayout chips = root.findViewById(R.id.ll_filters);
        TextView c = new TextView(requireContext());
        c.setText(label);
        c.setBackgroundResource(R.drawable.bg_chip);
        c.setTextColor(getResources().getColor(R.color.chip_text));
        c.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 10, 0); c.setLayoutParams(lp);
        c.setOnClickListener(v -> r.run());
        chips.addView(c);
    }

    private void applyFilter() {
        List<Roommate> result = new ArrayList<>();
        for (Roommate r : all) {
            if (!"Tất cả".equals(filterGender) && !filterGender.equals(r.gender)) continue;
            if (!"Tất cả".equals(filterLifestyle) && !filterLifestyle.equals(r.lifestyle)) continue;
            result.add(r);
        }
        adapter.setData(result);
    }
}
