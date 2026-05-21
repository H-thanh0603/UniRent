package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews")
public class Review {
    @PrimaryKey
    @NonNull
    public String reviewId;
    public String roomId;
    public String userId;
    public String userName;
    public float rating;
    public float locationRating;
    public float priceRating;
    public float securityRating;
    public float landlordRating;
    public float facilityRating;
    public String comment;
    public long createdAt;
    public Review() {}
}
