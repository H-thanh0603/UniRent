package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.unirent.app.database.Converters;
import java.util.List;

@Entity(tableName = "rooms")
@TypeConverters(Converters.class)
public class Room {
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_FULL = "full";
    public static final String APPROVED = "approved";
    public static final String PENDING = "pending";
    public static final String REJECTED = "rejected";

    @PrimaryKey
    @NonNull
    public String roomId;
    public String landlordId;
    public String title;
    public String description;
    public long price;
    public long deposit;
    public int area;
    public String address;
    public double latitude;
    public double longitude;
    public String roomType;
    public int maxPeople;
    public String status;
    public float ratingAverage;
    public int totalReviews;
    public int viewCount;
    public int favoriteCount;
    public long createdAt;
    public long updatedAt;
    public String approvedStatus;
    public String coverImageUrl;
    public List<String> imageUrls;
    public List<String> amenities;
    public double distanceToSchool;
    public String schoolName;
    public long electricFee;
    public long waterFee;
    public String rules;

    public Room() {}
}
