package com.unirent.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.unirent.app.R;
import java.util.*;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 vp;
    private LinearLayout dots;
    private Button btnNext;

    private static final int[][] PAGES = {
        {R.drawable.ic_home, R.string.onboard_title_1, R.string.onboard_desc_1},
        {R.drawable.ic_people, R.string.onboard_title_2, R.string.onboard_desc_2},
        {R.drawable.ic_map, R.string.onboard_title_3, R.string.onboard_desc_3},
    };

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        // Already onboarded?
        if (getSharedPreferences("unirent", MODE_PRIVATE).getBoolean("onboarded", false)) {
            goToLogin(); return;
        }
        setContentView(R.layout.activity_onboarding);
        vp = findViewById(R.id.vp_onboard);
        dots = findViewById(R.id.dots);
        btnNext = findViewById(R.id.btn_next);

        vp.setAdapter(new OnboardAdapter());
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int pos) {
                updateDots(pos);
                btnNext.setText(pos == PAGES.length - 1 ? "Bắt đầu" : "Tiếp tục");
            }
        });
        btnNext.setOnClickListener(v -> {
            int pos = vp.getCurrentItem();
            if (pos < PAGES.length - 1) vp.setCurrentItem(pos + 1, true);
            else finishOnboarding();
        });
        updateDots(0);
    }

    private void updateDots(int active) {
        dots.removeAllViews();
        for (int i = 0; i < PAGES.length; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(i == active ? R.drawable.dot_active : R.drawable.dot_inactive);
            dot.setPadding(8, 0, 8, 0);
            dots.addView(dot);
        }
    }

    private void finishOnboarding() {
        getSharedPreferences("unirent", MODE_PRIVATE).edit().putBoolean("onboarded", true).apply();
        goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    class OnboardAdapter extends RecyclerView.Adapter<OnboardAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_onboard, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            h.iv.setImageResource(PAGES[pos][0]);
            h.tvTitle.setText(PAGES[pos][1]);
            h.tvDesc.setText(PAGES[pos][2]);
        }
        @Override public int getItemCount() { return PAGES.length; }
        class VH extends RecyclerView.ViewHolder {
            ImageView iv; TextView tvTitle, tvDesc;
            VH(View v) { super(v); iv = v.findViewById(R.id.iv_illustration); tvTitle = v.findViewById(R.id.tv_title); tvDesc = v.findViewById(R.id.tv_desc); }
        }
    }
}
