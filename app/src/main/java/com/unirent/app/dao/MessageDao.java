package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Conversation;
import com.unirent.app.models.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertConversation(Conversation c);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(Message m);

    @Query("SELECT * FROM conversations WHERE studentId = :uid OR landlordId = :uid ORDER BY lastMessageAt DESC")
    List<Conversation> getConversations(String uid);

    @Query("SELECT * FROM conversations WHERE conversationId = :id LIMIT 1")
    Conversation getConversation(String id);

    @Query("SELECT * FROM messages WHERE conversationId = :id ORDER BY createdAt ASC")
    List<Message> getMessages(String id);
}
