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
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_home, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        User u = new SessionManager(requireContext()).getCurrentUser();

        TextView tvHello = root.findViewById(R.id.tv_hello);
        tvHello.setText("Xin chào, " + (u != null ? u.fullName.split(" ")[u.fullName.split(" ").length-1] : "bạn") + " 👋");

        // AI suggestions
        LinearLayout aiBox = root.findViewById(R.id.ll_ai_chips);
        for (String tip : AiHelper.aiSuggestions("student", u != null ? u.schoolName : null)) {
            TextView c = new TextView(requireContext());
            c.setText(tip);
            c.setBackgroundResource(R.drawable.bg_chip_blue);
            c.setTextColor(getResources().getColor(R.color.badge_blue_text));
            c.setPadding(24, 12, 24, 12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 12, 0);
            c.setLayoutParams(lp);
            aiBox.addView(c);
        }

        // Newest rooms - horizontal
        RecyclerView rvNew = root.findViewById(R.id.rv_newest);
        rvNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RoomAdapter aNew = new RoomAdapter(); aNew.setHorizontal(true);
        aNew.setData(db.roomDao().getNewest(10));
        rvNew.setAdapter(aNew);

        // Top rated
        RecyclerView rvTop = root.findViewById(R.id.rv_top);
        rvTop.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RoomAdapter aTop = new RoomAdapter(); aTop.setHorizontal(true);
        aTop.setData(db.roomDao().getTopRated(10));
        rvTop.setAdapter(aTop);

        // Cheapest
        RecyclerView rvCheap = root.findViewById(R.id.rv_cheap);
        rvCheap.setLayoutManager(new LinearLayoutManager(getContext()));
        RoomAdapter aCheap = new RoomAdapter();
        aCheap.setData(db.roomDao().getCheapest(10));
        rvCheap.setAdapter(aCheap);

        return root;
    }
}
