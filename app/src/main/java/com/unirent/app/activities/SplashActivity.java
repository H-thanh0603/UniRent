package com.unirent.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.unirent.app.R;
import com.unirent.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        try {
            setContentView(R.layout.activity_splash);
        } catch (Exception e) {
            // Fallback: skip splash, go directly to target
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                SessionManager sm = new SessionManager(this);
                Intent i;
                if (sm.isLoggedIn() && sm.getCurrentUser() != null) {
                    i = new Intent(this, MainActivity.class);
                } else {
                    // Check onboarding
                    boolean onboarded = getSharedPreferences("unirent", MODE_PRIVATE).getBoolean("onboarded", false);
                    i = new Intent(this, onboarded ? LoginActivity.class : OnboardingActivity.class);
                }
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception e) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}
