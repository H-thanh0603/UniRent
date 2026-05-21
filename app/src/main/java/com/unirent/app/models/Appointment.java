package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {
    public static final String PENDING = "pending";
    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";
    public static final String CANCELLED = "cancelled";
    public static final String COMPLETED = "completed";

    @PrimaryKey
    @NonNull
    public String appointmentId;
    public String roomId;
    public String studentId;
    public String landlordId;
    public long appointmentTime;
    public String note;
    public String status;
    public long createdAt;
    public long updatedAt;
    public Appointment() {}
}
