package com.unirent.app;

import android.app.Application;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.utils.SeedData;
import com.unirent.app.utils.SeedRoommates;

public class UniRentApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        AppDatabase db = AppDatabase.getInstance(this);
        if (db.userDao().count() == 0) {
            SeedData.seed(db);
        }
        if (db.roommateDao().count() == 0) {
            SeedRoommates.seed(db);
        }
    }
}
