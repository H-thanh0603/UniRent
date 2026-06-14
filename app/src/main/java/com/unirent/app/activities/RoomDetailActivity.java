package com.unirent.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;
import com.unirent.app.ai.AiHelper;
import com.unirent.app.utils.*;

import java.text.NumberFormat;
import java.util.*;

public class RoomDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    private Room room;
    private User user;
    private AppDatabase db;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_room_detail);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        db = AppDatabase.getInstance(this);
        user = new SessionManager(this).getCurrentUser();
        String id = getIntent().getStringExtra(EXTRA_ROOM_ID);
        room = db.roomDao().getById(id);
        if (room == null) { finish(); return; }
        room.viewCount++;
        db.roomDao().update(room);
        bind();
    }

    private void bind() {
        ImageView img = findViewById(R.id.iv_cover);
        Glide.with(this).load(room.coverImageUrl).placeholder(R.drawable.placeholder_room).into(img);
        ((TextView)findViewById(R.id.tv_title)).setText(room.title);
        ((TextView)findViewById(R.id.tv_price)).setText(FormatUtils.money(room.price) + "/tháng");
        ((TextView)findViewById(R.id.tv_address)).setText(room.address);
        ((TextView)findViewById(R.id.tv_area)).setText(room.area + " m²");
        ((TextView)findViewById(R.id.tv_distance)).setText("Cách trường " + room.distanceToSchool + " km");
        ((TextView)findViewById(R.id.tv_school)).setText(room.schoolName);
        ((TextView)findViewById(R.id.tv_rating)).setText(String.format(Locale.US, "%.1f (%d đánh giá)", room.ratingAverage, room.totalReviews));
        ((TextView)findViewById(R.id.tv_description)).setText(room.description);
        ((TextView)findViewById(R.id.tv_electric)).setText(FormatUtils.money(room.electricFee) + "đ/kWh");
        ((TextView)findViewById(R.id.tv_water)).setText(FormatUtils.money(room.waterFee) + "đ/người");
        ((TextView)findViewById(R.id.tv_rules)).setText(room.rules);

        FlexboxLayout box = findViewById(R.id.fb_amenities);
        box.removeAllViews();
        if (room.amenities != null) {
            for (String a : room.amenities) {
                TextView chip = new TextView(this);
                chip.setText(a);
                chip.setBackgroundResource(R.drawable.bg_chip);
                chip.setTextColor(getResources().getColor(R.color.badge_blue_text));
                chip.setPadding(24, 12, 24, 12);
                FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 12, 12);
                chip.setLayoutParams(lp);
                box.addView(chip);
            }
        }

        // AI risk warning
        TextView tvRisk = findViewById(R.id.tv_risk);
        String warn = AiHelper.riskWarning(room);
        if (warn != null) { tvRisk.setText(warn); tvRisk.setVisibility(View.VISIBLE); }
        else tvRisk.setVisibility(View.GONE);

        // Favorite
        ImageButton btnFav = findViewById(R.id.btn_favorite);
        isFavorite = user != null && db.favoriteDao().exists(user.userId, room.roomId);
        btnFav.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        btnFav.setOnClickListener(v -> toggleFav(btnFav));

        findViewById(R.id.btn_chat).setOnClickListener(v -> openChat());
        findViewById(R.id.btn_call).setOnClickListener(v -> {
            User l = db.userDao().getById(room.landlordId);
            if (l != null && !TextUtils.isEmpty(l.phone)) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + l.phone)));
            }
        });
        findViewById(R.id.btn_book).setOnClickListener(v -> bookViewing());
        findViewById(R.id.btn_report).setOnClickListener(v -> report());
    }

    private void toggleFav(ImageButton btn) {
        if (user == null) return;
        if (isFavorite) {
            db.favoriteDao().delete(user.userId, room.roomId);
            isFavorite = false;
        } else {
            Favorite f = new Favorite();
            f.userId = user.userId; f.roomId = room.roomId;
            f.createdAt = System.currentTimeMillis();
            db.favoriteDao().insert(f);
            isFavorite = true;
        }
        btn.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        Toast.makeText(this, isFavorite ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
    }

    private void openChat() {
        if (user == null) return;
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.EXTRA_ROOM_ID, room.roomId);
        i.putExtra(ChatActivity.EXTRA_OTHER_ID, room.landlordId);
        startActivity(i);
    }

    private void bookViewing() {
        if (user == null) return;
        Intent i = new Intent(this, AppointmentActivity.class);
        i.putExtra(AppointmentActivity.EXTRA_ROOM_ID, room.roomId);
        startActivity(i);
    }

    private void report() {
        final String[] reasons = {"Phòng ảo","Sai giá","Ảnh không đúng thực tế","Chủ trọ lừa đảo","Đã hết phòng nhưng vẫn đăng","Khác"};
        new AlertDialog.Builder(this)
            .setTitle("Lý do báo cáo")
            .setItems(reasons, (d, w) -> {
                Report r = new Report();
                r.reportId = "rpt-" + UUID.randomUUID().toString().substring(0,8);
                r.roomId = room.roomId; r.reporterId = user.userId;
                r.reason = reasons[w]; r.status = Report.PENDING;
                r.createdAt = System.currentTimeMillis();
                db.reportDao().insert(r);
                Toast.makeText(this, "Đã gửi báo cáo. Cảm ơn bạn!", Toast.LENGTH_SHORT).show();
            }).show();
    }
}
