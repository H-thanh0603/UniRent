package com.unirent.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";

    @PrimaryKey
    @NonNull
    public String messageId;
    public String conversationId;
    public String senderId;
    public String receiverId;
    public String roomId;
    public String messageType;
    public String content;
    public String imageUrl;
    public boolean isRead;
    public long createdAt;
    public Message() {}
}
