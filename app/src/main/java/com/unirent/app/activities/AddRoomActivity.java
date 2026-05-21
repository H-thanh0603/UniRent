package com.unirent.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.unirent.app.R;
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import com.unirent.app.utils.SessionManager;

import java.util.*;

public class AddRoomActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";

    private static final String[] AMENITIES = {
        "Wifi","Máy lạnh","Toilet riêng","Gác lửng","Bếp","Tủ lạnh",
        "Máy giặt","Ban công","Chỗ để xe","Camera","Bảo vệ","Cổng riêng","Khóa vân tay"
    };

    private TextInputEditText etTitle, etDesc, etPrice, etArea, etAddress, etSchool, etDistance;
    private FlexboxLayout fbAmenities;
    private final Set<String> chosen = new HashSet<>();
    private AppDatabase db;
    private Room editing;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_room);
        db = AppDatabase.getInstance(this);

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);
        etPrice = findViewById(R.id.et_price);
        etArea = findViewById(R.id.et_area);
        etAddress = findViewById(R.id.et_address);
        etSchool = findViewById(R.id.et_school);
        etDistance = findViewById(R.id.et_distance);
        fbAmenities = findViewById(R.id.fb_amenities);

        renderAmenityChips();

        String id = getIntent().getStringExtra(EXTRA_ROOM_ID);
        if (id != null) {
            editing = db.roomDao().getById(id);
            if (editing != null) prefill();
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_ai_describe).setOnClickListener(v -> aiDescribe());
        findViewById(R.id.btn_submit).setOnClickListener(v -> submit());
    }

    private void renderAmenityChips() {
        fbAmenities.removeAllViews();
        for (String a : AMENITIES) {
            TextView c = new TextView(this);
            c.setText(a);
            c.setPadding(24, 12, 24, 12);
            c.setBackgroundResource(R.drawable.bg_chip_blue);
            c.setTextColor(getResources().getColor(R.color.badge_blue_text));
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 12, 12);
            c.setLayoutParams(lp);
            updateChipState(c, a);
            c.setOnClickListener(v -> {
                if (chosen.contains(a)) chosen.remove(a); else chosen.add(a);
                updateChipState(c, a);
            });
            fbAmenities.addView(c);
        }
    }

    private void updateChipState(TextView c, String a) {
        if (chosen.contains(a)) {
            c.setBackgroundResource(R.drawable.bg_chip_green);
            c.setTextColor(getResources().getColor(R.color.badge_green_text));
        } else {
            c.setBackgroundResource(R.drawable.bg_chip_blue);
            c.setTextColor(getResources().getColor(R.color.badge_blue_text));
        }
    }

    private void prefill() {
        etTitle.setText(editing.title);
        etDesc.setText(editing.description);
        etPrice.setText(String.valueOf(editing.price));
        etArea.setText(String.valueOf(editing.area));
        etAddress.setText(editing.address);
        etSchool.setText(editing.schoolName);
        etDistance.setText(String.valueOf(editing.distanceToSchool));
        if (editing.amenities != null) chosen.addAll(editing.amenities);
        renderAmenityChips();
    }

    private void aiDescribe() {
        try {
            long price = Long.parseLong(etPrice.getText().toString().trim());
            int area = Integer.parseInt(etArea.getText().toString().trim());
            String desc = AiHelper.generateDescription(area, new ArrayList<>(chosen), price);
            etDesc.setText(desc);
        } catch (Exception e) {
            Toast.makeText(this, "Hãy nhập diện tích và giá trước", Toast.LENGTH_SHORT).show();
        }
    }

    private String tx(TextInputEditText t) { return t.getText() == null ? "" : t.getText().toString().trim(); }

    private void submit() {
        String title = tx(etTitle), desc = tx(etDesc), addr = tx(etAddress), school = tx(etSchool);
        String priceS = tx(etPrice), areaS = tx(etArea), distS = tx(etDistance);
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(addr) || TextUtils.isEmpty(priceS) || TextUtils.isEmpty(areaS)) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            long price = Long.parseLong(priceS);
            int area = Integer.parseInt(areaS);
            double dist = TextUtils.isEmpty(distS) ? 0 : Double.parseDouble(distS);

            Room r = editing != null ? editing : new Room();
            if (editing == null) {
                r.roomId = "room-" + UUID.randomUUID().toString().substring(0,8);
                r.landlordId = new SessionManager(this).getUserId();
                r.createdAt = System.currentTimeMillis();
                r.viewCount = 0;
                r.favoriteCount = 0;
                r.ratingAverage = 0;
                r.totalReviews = 0;
            }
            r.title = title; r.description = desc; r.price = price; r.deposit = price;
            r.area = area; r.address = addr; r.schoolName = school; r.distanceToSchool = dist;
            r.status = Room.STATUS_AVAILABLE;
            r.approvedStatus = Room.PENDING;
            r.amenities = new ArrayList<>(chosen);
            r.coverImageUrl = "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800";
            r.imageUrls = Arrays.asList(r.coverImageUrl);
            r.electricFee = 3500; r.waterFee = 80000;
            r.rules = "Giờ giấc tự do. Tuân thủ nội quy chung.";
            r.roomType = "Phòng đơn"; r.maxPeople = 2;
            r.updatedAt = System.currentTimeMillis();

            if (editing == null) db.roomDao().insert(r); else db.roomDao().update(r);
            Toast.makeText(this, "Đã gửi tin đăng. Chờ admin duyệt.", Toast.LENGTH_LONG).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá / diện tích phải là số", Toast.LENGTH_SHORT).show();
        }
    }
}
