package com.unirent.app.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.models.Roommate;
import java.util.*;

public class RoommateAdapter extends RecyclerView.Adapter<RoommateAdapter.VH> {
    private List<Roommate> data = new ArrayList<>();
    private OnClickListener listener;

    public interface OnClickListener { void onClick(Roommate r); }
    public void setOnClickListener(OnClickListener l) { listener = l; }

    public void setData(List<Roommate> list) { data.clear(); if (list != null) data.addAll(list); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_roommate, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Roommate r = data.get(pos);
        h.emoji.setText(r.avatarEmoji);
        h.name.setText(r.fullName + ", " + r.age);
        h.school.setText(r.schoolName);
        h.budget.setText(String.format(Locale.US, "%,d - %,dđ", r.budgetMin, r.budgetMax));
        h.area.setText(r.preferredArea);
        h.lifestyle.setText(r.lifestyle + " • " + r.sleepSchedule + " • " + r.cleanliness);
        h.bio.setText(r.bio);
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(r); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView emoji, name, school, budget, area, lifestyle, bio;
        VH(@NonNull View v) { super(v);
            emoji = v.findViewById(R.id.tv_emoji); name = v.findViewById(R.id.tv_name);
            school = v.findViewById(R.id.tv_school); budget = v.findViewById(R.id.tv_budget);
            area = v.findViewById(R.id.tv_area); lifestyle = v.findViewById(R.id.tv_lifestyle);
            bio = v.findViewById(R.id.tv_bio);
        }
    }
}
