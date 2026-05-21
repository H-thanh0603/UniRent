package com.unirent.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unirent.app.R;
import com.unirent.app.fragments.*;
import com.unirent.app.models.User;
import com.unirent.app.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        SessionManager sm = new SessionManager(this);
        User u = sm.getCurrentUser();
        if (u == null) {
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish(); return;
        }
        if (User.ROLE_LANDLORD.equals(u.role)) {
            bottomNav.inflateMenu(R.menu.menu_landlord);
            replace(new LandlordDashboardFragment());
        } else if (User.ROLE_ADMIN.equals(u.role)) {
            bottomNav.inflateMenu(R.menu.menu_admin);
            replace(new AdminPendingFragment());
        } else {
            bottomNav.inflateMenu(R.menu.menu_student);
            replace(new HomeFragment());
        }
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment f = pickFragment(id, u.role);
            if (f != null) { replace(f); return true; }
            return false;
        });
    }

    private Fragment pickFragment(int id, String role) {
        if (User.ROLE_STUDENT.equals(role)) {
            if (id == R.id.nav_home) return new HomeFragment();
            if (id == R.id.nav_search) return new SearchFragment();
            if (id == R.id.nav_favorite) return new FavoriteFragment();
            if (id == R.id.nav_profile) return new ProfileFragment();
        } else if (User.ROLE_LANDLORD.equals(role)) {
            if (id == R.id.nav_dashboard) return new LandlordDashboardFragment();
            if (id == R.id.nav_my_rooms) return new LandlordRoomsFragment();
            if (id == R.id.nav_appointments) return new LandlordAppointmentsFragment();
            if (id == R.id.nav_profile) return new ProfileFragment();
        } else if (User.ROLE_ADMIN.equals(role)) {
            if (id == R.id.nav_pending) return new AdminPendingFragment();
            if (id == R.id.nav_users) return new AdminUsersFragment();
            if (id == R.id.nav_profile) return new ProfileFragment();
        }
        return null;
    }

    private void replace(Fragment f) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, f).commit();
    }
}
