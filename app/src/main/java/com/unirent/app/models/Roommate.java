package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "roommates")
public class Roommate {
    @PrimaryKey @NonNull
    public String profileId;
    public String userId;
    public String fullName;
    public int age;
    public String gender;
    public String schoolName;
    public int budgetMin;
    public int budgetMax;
    public String preferredArea;
    public String lifestyle;     // "Sáng tạo", "Yên tĩnh", "Năng động"
    public String sleepSchedule; // "Sớm", "Bình thường", "Cú đêm"
    public String cleanliness;   // "Sạch sẽ", "Bình thường", "Thoải mái"
    public String bio;
    public String avatarEmoji;
    public long createdAt;

    public Roommate() {}
}
