package com.unirent.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;
import com.unirent.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class AppointmentActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    private final Calendar selected = Calendar.getInstance();
    private TextView tvDate, tvTime;
    private TextInputEditText etNote;
    private Room room;
    private User me;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_appointment);
        db = AppDatabase.getInstance(this);
        me = new SessionManager(this).getCurrentUser();
        room = db.roomDao().getById(getIntent().getStringExtra(EXTRA_ROOM_ID));
        if (room == null || me == null) { finish(); return; }

        ((TextView)findViewById(R.id.tv_room_title)).setText(room.title);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        etNote = findViewById(R.id.et_note);

        selected.add(Calendar.DAY_OF_MONTH, 1);
        selected.set(Calendar.HOUR_OF_DAY, 18);
        selected.set(Calendar.MINUTE, 0);
        refresh();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_pick_date).setOnClickListener(v -> pickDate());
        findViewById(R.id.btn_pick_time).setOnClickListener(v -> pickTime());
        findViewById(R.id.btn_submit).setOnClickListener(v -> submit());
    }

    private void refresh() {
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(selected.getTime()));
        tvTime.setText(new SimpleDateFormat("HH:mm", Locale.US).format(selected.getTime()));
    }

    private void pickDate() {
        new DatePickerDialog(this, (v, y, m, d) -> {
            selected.set(y, m, d); refresh();
        }, selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime() {
        new TimePickerDialog(this, (v, h, m) -> {
            selected.set(Calendar.HOUR_OF_DAY, h); selected.set(Calendar.MINUTE, m); refresh();
        }, selected.get(Calendar.HOUR_OF_DAY), selected.get(Calendar.MINUTE), true).show();
    }

    private void submit() {
        if (selected.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(this, "Vui lòng chọn thời gian trong tương lai", Toast.LENGTH_SHORT).show();
            return;
        }
        Appointment a = new Appointment();
        a.appointmentId = "apt-" + UUID.randomUUID().toString().substring(0,8);
        a.roomId = room.roomId;
        a.studentId = me.userId;
        a.landlordId = room.landlordId;
        a.appointmentTime = selected.getTimeInMillis();
        a.note = etNote.getText() == null ? "" : etNote.getText().toString();
        a.status = Appointment.PENDING;
        a.createdAt = System.currentTimeMillis();
        a.updatedAt = a.createdAt;
        db.appointmentDao().insert(a);
        Toast.makeText(this, "Đã gửi yêu cầu xem phòng. Chờ chủ trọ xác nhận.", Toast.LENGTH_LONG).show();
        finish();
    }
}
