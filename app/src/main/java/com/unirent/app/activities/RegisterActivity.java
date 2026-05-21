package com.unirent.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.unirent.app.R;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPhone, etPwd, etSchool;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPwd = findViewById(R.id.et_password);
        etSchool = findViewById(R.id.et_school);
        rgRole = findViewById(R.id.rg_role);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_submit).setOnClickListener(v -> submit());
    }

    private String tx(TextInputEditText t) { return t.getText() == null ? "" : t.getText().toString().trim(); }

    private void submit() {
        String name = tx(etName), email = tx(etEmail), phone = tx(etPhone), pwd = tx(etPwd), school = tx(etSchool);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() < 6) {
            Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        AppDatabase db = AppDatabase.getInstance(this);
        if (db.userDao().getByEmail(email) != null) {
            Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }
        User u = new User();
        u.userId = "u-" + UUID.randomUUID().toString().substring(0, 8);
        u.fullName = name; u.email = email; u.phone = phone; u.password = pwd;
        u.schoolName = school; u.status = "active";
        u.role = (rgRole.getCheckedRadioButtonId() == R.id.rb_landlord)
                 ? User.ROLE_LANDLORD : User.ROLE_STUDENT;
        u.createdAt = System.currentTimeMillis();
        db.userDao().insert(u);
        new SessionManager(this).setLoggedIn(u.userId);
        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        startActivity(new android.content.Intent(this, MainActivity.class));
        finishAffinity();
    }
}
