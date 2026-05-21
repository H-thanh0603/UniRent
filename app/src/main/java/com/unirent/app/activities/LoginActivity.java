package com.unirent.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPwd;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPwd = findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(v -> doLogin());
        findViewById(R.id.btn_register).setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));

        // Quick login chips
        findViewById(R.id.btn_demo_student).setOnClickListener(v -> fill("thanh@student.vn", "123456"));
        findViewById(R.id.btn_demo_landlord).setOnClickListener(v -> fill("lan@landlord.vn", "123456"));
        findViewById(R.id.btn_demo_admin).setOnClickListener(v -> fill("admin@unirent.vn", "123456"));
    }

    private void fill(String em, String pw) {
        etEmail.setText(em); etPwd.setText(pw);
    }

    private void doLogin() {
        String em = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String pw = etPwd.getText() == null ? "" : etPwd.getText().toString();
        if (TextUtils.isEmpty(em) || TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        User u = AppDatabase.getInstance(this).userDao().login(em, pw);
        if (u == null) {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        new SessionManager(this).setLoggedIn(u.userId);
        Toast.makeText(this, "Xin chào, " + u.fullName, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
