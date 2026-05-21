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
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        int layout = horizontal ? R.layout.item_room_horizontal : R.layout.item_room;
        return new VH(LayoutInflater.from(p.getContext()).inflate(layout, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Room r = data.get(pos);
        Glide.with(h.itemView).load(r.coverImageUrl).placeholder(R.drawable.placeholder_room).into(h.img);
        h.title.setText(r.title);
        h.address.setText(r.address);
        h.price.setText(FormatUtils.money(r.price) + "đ/tháng");
        if (h.area != null) h.area.setText(r.area + " m²");
        if (h.distance != null) h.distance.setText(String.format(Locale.US, "%.1f km", r.distanceToSchool));
        if (h.rating != null) h.rating.setText(String.format(Locale.US, "★ %.1f", r.ratingAverage));
        if (h.status != null) {
            if (Room.STATUS_AVAILABLE.equals(r.status)) {
                h.status.setText("Còn phòng");
                h.status.setBackgroundResource(R.drawable.bg_chip_green);
                h.status.setTextColor(h.itemView.getContext().getResources().getColor(R.color.badge_green_text));
            } else {
                h.status.setText("Hết phòng");
                h.status.setBackgroundResource(R.drawable.bg_chip_red);
                h.status.setTextColor(h.itemView.getContext().getResources().getColor(R.color.badge_red_text));
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
        }
    }
}
