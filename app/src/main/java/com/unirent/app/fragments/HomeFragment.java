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
import com.unirent.app.activities.ChatActivity;
import com.unirent.app.adapters.RoomAdapter;
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_home, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        User u = new SessionManager(requireContext()).getCurrentUser();

        ((TextView) root.findViewById(R.id.tv_hello)).setText(
            "Xin chào, " + (u != null ? u.fullName.split(" ")[u.fullName.split(" ").length-1] : "bạn") + " 👋");

        // Chat AI
        root.findViewById(R.id.btn_ai_chat).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(ChatActivity.EXTRA_AI_MODE, true)));

        // Area Advisor → switch to Area tab
        root.findViewById(R.id.btn_area_advisor).setOnClickListener(v -> {
            if (getActivity() instanceof com.unirent.app.activities.MainActivity)
                ((com.unirent.app.activities.MainActivity) getActivity()).switchToArea();
        });

        // Search card → switch to search
        root.findViewById(R.id.card_search).setOnClickListener(v -> {
            if (getActivity() instanceof com.unirent.app.activities.MainActivity)
                ((com.unirent.app.activities.MainActivity) getActivity()).switchToSearch();
        });

        // AI chips
        LinearLayout aiBox = root.findViewById(R.id.ll_ai_chips);
        for (String tip : AiHelper.aiSuggestions("student", u != null ? u.schoolName : null)) {
            TextView c = new TextView(requireContext());
            c.setText(tip);
            c.setBackgroundResource(R.drawable.bg_pill);
            c.setTextColor(getResources().getColor(R.color.chip_text));
            c.setPadding(16, 8, 16, 8);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0); c.setLayoutParams(lp);
            c.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(ChatActivity.EXTRA_AI_MODE, true)));
            aiBox.addView(c);
        }

        // Newest
        RecyclerView rvNew = root.findViewById(R.id.rv_newest);
        rvNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RoomAdapter aNew = new RoomAdapter(); aNew.setHorizontal(true);
        aNew.setData(db.roomDao().getNewest(10)); rvNew.setAdapter(aNew);

        // Top rated
        RecyclerView rvTop = root.findViewById(R.id.rv_top);
        rvTop.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RoomAdapter aTop = new RoomAdapter(); aTop.setHorizontal(true);
        aTop.setData(db.roomDao().getTopRated(10)); rvTop.setAdapter(aTop);

        // Cheapest
        RecyclerView rvCheap = root.findViewById(R.id.rv_cheap);
        rvCheap.setLayoutManager(new LinearLayoutManager(getContext()));
        RoomAdapter aCheap = new RoomAdapter();
        aCheap.setData(db.roomDao().getCheapest(10)); rvCheap.setAdapter(aCheap);

        return root;
    }
}
