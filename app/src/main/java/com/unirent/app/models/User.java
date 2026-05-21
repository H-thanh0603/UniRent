package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_LANDLORD = "landlord";
    public static final String ROLE_ADMIN = "admin";

    @PrimaryKey
    @NonNull
    public String userId;
    public String fullName;
    public String email;
    public String phone;
    public String password;
    public String avatarUrl;
    public String role;
    public String schoolName;
    public String address;
    public long createdAt;
    public String status;

    public User() {}
}
