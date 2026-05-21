package com.unirent.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.unirent.app.R;

public class AdminActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        // Admin uses MainActivity flow with admin menu; this is reserved for direct deep-link.
    }
}
