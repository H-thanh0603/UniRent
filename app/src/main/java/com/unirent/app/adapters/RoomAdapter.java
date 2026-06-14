package com.unirent.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.unirent.app.R;
import com.unirent.app.activities.RoomDetailActivity;
import com.unirent.app.models.Room;
import com.unirent.app.utils.FormatUtils;
import java.util.*;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.VH> {
    public interface OnLongClick { void onLong(Room r); }
    private final List<Room> data = new ArrayList<>();
    private boolean horizontal = false;
    private OnLongClick onLong;

    public void setHorizontal(boolean h) { this.horizontal = h; }
    public void setOnLongClick(OnLongClick l) { this.onLong = l; }
    public void setData(List<Room> list) {
        data.clear(); if (list != null) data.addAll(list); notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        int layout = horizontal ? R.layout.item_room_horizontal : R.layout.item_room;
        return new VH(LayoutInflater.from(p.getContext()).inflate(layout, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Room r = data.get(pos);
        Glide.with(h.itemView).load(r.coverImageUrl).placeholder(R.drawable.placeholder_room).into(h.img);
        h.title.setText(r.title);
        if (h.address != null) h.address.setText(r.address);
        h.price.setText(FormatUtils.money(r.price) + "đ/tháng");
        if (h.rating != null) h.rating.setText(String.format(Locale.US, "★ %.1f", r.ratingAverage));
        if (h.area != null) h.area.setText(r.area + " m²");
        if (h.distance != null) h.distance.setText(String.format(Locale.US, "%.1f km", r.distanceToSchool));
        if (h.status != null) {
            boolean avail = Room.STATUS_AVAILABLE.equals(r.status);
            h.status.setText(avail ? "Còn phòng" : "Hết phòng");
            h.status.setBackgroundResource(avail ? R.drawable.bg_chip_green : R.drawable.bg_chip_red);
            h.status.setTextColor(h.itemView.getContext().getResources().getColor(avail ? R.color.badge_green_text : R.color.badge_red_text));
        }
        // Amenities chips
        if (h.llAmenities != null && r.amenities != null) {
            h.llAmenities.removeAllViews();
            int max = Math.min(r.amenities.size(), 3);
            for (int i = 0; i < max; i++) {
                TextView chip = new TextView(h.itemView.getContext());
                chip.setText(r.amenities.get(i));
                chip.setTextColor(h.itemView.getContext().getResources().getColor(R.color.chip_text));
                chip.setBackgroundResource(R.drawable.bg_pill);
                chip.setTextSize(11);
                chip.setPadding(10, 3, 10, 3);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 6, 0);
                chip.setLayoutParams(lp);
                h.llAmenities.addView(chip);
            }
        }
        h.itemView.setOnClickListener(v -> {
            Context c = v.getContext();
            Intent i = new Intent(c, RoomDetailActivity.class);
            i.putExtra(RoomDetailActivity.EXTRA_ROOM_ID, r.roomId);
            c.startActivity(i);
        });
        h.itemView.setOnLongClickListener(v -> {
            if (onLong != null) { onLong.onLong(r); return true; }
            return false;
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, price, address, area, distance, rating, status;
        LinearLayout llAmenities;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.iv_cover);
            title = v.findViewById(R.id.tv_title);
            price = v.findViewById(R.id.tv_price);
            address = v.findViewById(R.id.tv_address);
            area = v.findViewById(R.id.tv_area);
            distance = v.findViewById(R.id.tv_distance);
            rating = v.findViewById(R.id.tv_rating);
            status = v.findViewById(R.id.tv_status);
            llAmenities = v.findViewById(R.id.ll_amenities);
        }
    }
}
