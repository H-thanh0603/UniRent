package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversations")
public class Conversation {
    @PrimaryKey
    @NonNull
    public String conversationId;
    public String studentId;
    public String landlordId;
    public String roomId;
    public String lastMessage;
    public long lastMessageAt;
    public String studentName;
    public String landlordName;
    public String roomTitle;
    public Conversation() {}
}
