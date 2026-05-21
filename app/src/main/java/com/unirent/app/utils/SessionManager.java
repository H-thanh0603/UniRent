package com.unirent.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.unirent.app.models.User;
import com.unirent.app.database.AppDatabase;

public class SessionManager {
    private static final String PREF = "unirent_pref";
    private static final String KEY_UID = "uid";
    private final SharedPreferences sp;
    private final Context ctx;

    public SessionManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        sp = this.ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(String userId) {
        sp.edit().putString(KEY_UID, userId).apply();
    }

    public String getUserId() {
        return sp.getString(KEY_UID, null);
    }

    public boolean isLoggedIn() { return getUserId() != null; }

    public void logout() {
        sp.edit().remove(KEY_UID).apply();
    }

    public User getCurrentUser() {
        String uid = getUserId();
        if (uid == null) return null;
        return AppDatabase.getInstance(ctx).userDao().getById(uid);
    }
}
