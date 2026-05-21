package com.unirent.app.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.unirent.app.dao.*;
import com.unirent.app.models.*;

@Database(
    entities = {
        User.class,
        com.unirent.app.models.Room.class,
        Favorite.class,
        Appointment.class,
        Message.class,
        Conversation.class,
        Review.class,
        Report.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract RoomDao roomDao();
    public abstract FavoriteDao favoriteDao();
    public abstract AppointmentDao appointmentDao();
    public abstract MessageDao messageDao();
    public abstract ReviewDao reviewDao();
    public abstract ReportDao reportDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "unirent.db"
                    ).fallbackToDestructiveMigration()
                     .allowMainThreadQueries()
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}
