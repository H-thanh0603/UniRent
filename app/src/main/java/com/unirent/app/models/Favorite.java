package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites", primaryKeys = {"userId", "roomId"})
public class Favorite {
    @NonNull public String userId = "";
    @NonNull public String roomId = "";
    public long createdAt;
    public Favorite() {}
}
