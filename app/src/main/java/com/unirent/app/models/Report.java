package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reports")
public class Report {
    public static final String PENDING = "pending";
    public static final String RESOLVED = "resolved";

    @PrimaryKey
    @NonNull
    public String reportId;
    public String roomId;
    public String reporterId;
    public String reason;
    public String description;
    public String status;
    public long createdAt;
    public long handledAt;
    public String handledBy;
    public Report() {}
}
