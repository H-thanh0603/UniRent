package com.unirent.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.unirent.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sm = new SessionManager(this);
            Intent i;
            if (sm.isLoggedIn() && sm.getCurrentUser() != null) {
                i = new Intent(this, MainActivity.class);
            } else {
                i = new Intent(this, LoginActivity.class);
            }
            startActivity(i);
            finish();
        }, 1200);
    }
}
