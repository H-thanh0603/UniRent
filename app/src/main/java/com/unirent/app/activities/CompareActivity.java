package com.unirent.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.unirent.app.R;
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import com.unirent.app.utils.FormatUtils;

import java.util.*;

public class CompareActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_IDS = "room_ids";

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_compare);
        ArrayList<String> ids = getIntent().getStringArrayListExtra(EXTRA_ROOM_IDS);
        if (ids == null || ids.isEmpty()) { finish(); return; }

        AppDatabase db = AppDatabase.getInstance(this);
        List<Room> rooms = new ArrayList<>();
        for (String id : ids) {
            Room r = db.roomDao().getById(id);
            if (r != null) rooms.add(r);
        }
        if (rooms.size() < 2) {
            Toast.makeText(this, "Cần ít nhất 2 phòng để so sánh", Toast.LENGTH_SHORT).show();
            finish(); return;
        }

        TableLayout tbl = findViewById(R.id.table);
        addRow(tbl, "Tiêu chí", names(rooms), true);
        addRow(tbl, "Giá", values(rooms, r -> FormatUtils.money(r.price) + "đ"), false);
        addRow(tbl, "Diện tích", values(rooms, r -> r.area + " m²"), false);
        addRow(tbl, "Cách trường", values(rooms, r -> r.distanceToSchool + " km"), false);
        addRow(tbl, "Đánh giá", values(rooms, r -> String.format(Locale.US, "%.1f ★", r.ratingAverage)), false);
        addRow(tbl, "Wifi", values(rooms, r -> has(r, "Wifi")), false);
        addRow(tbl, "Máy lạnh", values(rooms, r -> has(r, "Máy lạnh")), false);
        addRow(tbl, "Toilet riêng", values(rooms, r -> has(r, "Toilet riêng")), false);

        ((TextView)findViewById(R.id.tv_ai)).setText(AiHelper.compareSummary(rooms));
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    interface Mapper { String map(Room r); }

    private List<String> names(List<Room> rs) {
        List<String> l = new ArrayList<>();
        for (Room r : rs) l.add(r.title.length() > 16 ? r.title.substring(0, 16) + "…" : r.title);
        return l;
    }

    private List<String> values(List<Room> rs, Mapper m) {
        List<String> l = new ArrayList<>();
        for (Room r : rs) l.add(m.map(r));
        return l;
    }

    private String has(Room r, String key) {
        return r.amenities != null && r.amenities.contains(key) ? "✓" : "—";
    }

    private void addRow(TableLayout t, String label, List<String> values, boolean header) {
        TableRow row = new TableRow(this);
        row.addView(cell(label, true, header));
        for (String v : values) row.addView(cell(v, false, header));
        t.addView(row);
    }

    private TextView cell(String text, boolean bold, boolean header) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(20, 16, 20, 16);
        if (bold || header) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        if (header) tv.setBackgroundColor(getResources().getColor(R.color.brand_light));
        return tv;
    }
}
