package com.unirent.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.unirent.app.R;
import com.unirent.app.adapters.MessageAdapter;
import com.unirent.app.ai.AiHelper;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;
import com.unirent.app.utils.SessionManager;
import java.util.*;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_OTHER_ID = "other_id";
    public static final String EXTRA_AI_MODE = "ai_mode";

    private AppDatabase db;
    private User me, other;
    private String conversationId, roomId;
    private MessageAdapter adapter;
    private RecyclerView rv;
    private EditText etInput;
    private boolean isAiMode;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_chat);
        db = AppDatabase.getInstance(this);
        me = new SessionManager(this).getCurrentUser();
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        isAiMode = getIntent().getBooleanExtra(EXTRA_AI_MODE, false);
        if (roomId != null) currentRoom = db.roomDao().getById(roomId);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        rv = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(me != null ? me.userId : "user");
        rv.setAdapter(adapter);

        if (isAiMode) setupAiChat(toolbar);
        else setupUserChat(toolbar);
    }

    // ─── AI Chatbot mode ───

    private void setupAiChat(MaterialToolbar toolbar) {
        toolbar.setTitle("UniRent AI");
        findViewById(R.id.hsv_quick).setVisibility(View.GONE);
        addBubble("ai", "👋 Chào bạn! Tôi là trợ lý AI UniRent. Hỏi tôi bất cứ điều gì về tìm trọ nhé!");
        findViewById(R.id.btn_send).setOnClickListener(v -> sendAiMessage());
    }

    private void sendAiMessage() {
        String txt = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(txt)) return;
        addBubble(me.userId, txt);
        etInput.setText("");
        addTyping("ai");
        AiHelper.chatReply(txt, reply -> { removeTyping(); addBubble("ai", reply); });
    }

    // ─── User-to-User chat (có AI auto-reply) ───

    private void setupUserChat(MaterialToolbar toolbar) {
        String oid = getIntent().getStringExtra(EXTRA_OTHER_ID);
        other = db.userDao().getById(oid);
        if (me == null || other == null) {
            Toast.makeText(this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
            finish(); return;
        }
        toolbar.setTitle(other.fullName);
        toolbar.setSubtitle("💬 Chat với " + other.fullName + " — AI sẽ trả lời thay nếu chủ trọ bận");

        ensureConversation();
        loadMessages();
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        setupQuickChips();
    }

    private void setupQuickChips() {
        String[] quick = {"Phòng còn trống không ạ?", "Giá đã bao gồm điện nước chưa?",
                          "Có được nấu ăn không?", "Chiều nay xem phòng được không?"};
        LinearLayout chips = findViewById(R.id.ll_chips);
        for (String q : quick) {
            TextView c = new TextView(this);
            c.setText(q);
            c.setBackgroundResource(R.drawable.bg_chip);
            c.setTextColor(getResources().getColor(R.color.chip_text));
            c.setPadding(20, 10, 20, 10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0); c.setLayoutParams(lp);
            c.setOnClickListener(v -> { etInput.setText(q); etInput.setSelection(q.length()); });
            chips.addView(c);
        }
    }

    private void sendMessage() {
        String txt = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(txt)) return;

        // Lưu tin nhắn vào DB
        Message m = new Message();
        m.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
        m.conversationId = conversationId;
        m.senderId = me.userId; m.receiverId = other.userId;
        m.roomId = roomId; m.messageType = Message.TYPE_TEXT;
        m.content = txt; m.createdAt = System.currentTimeMillis();
        db.messageDao().insertMessage(m);

        Conversation c = db.messageDao().getConversation(conversationId);
        if (c != null) { c.lastMessage = txt; c.lastMessageAt = m.createdAt; db.messageDao().insertConversation(c); }

        etInput.setText("");
        loadMessages();

        // ⚡ AI auto-reply sau 2 giây (giả lập chủ trọ trả lời)
        handler.postDelayed(() -> aiAutoReply(txt), 1500);
    }

    private void aiAutoReply(String userMsg) {
        addTyping(other.userId);

        String context = "Bạn là " + other.fullName + ", một chủ trọ. ";
        if (currentRoom != null) {
            context += "Bạn có phòng \"" + currentRoom.title + "\" giá "
                + String.format(Locale.US, "%,dđ", currentRoom.price)
                + ", diện tích " + currentRoom.area + "m². ";
        }
        context += "Người thuê vừa nhắn: \"" + userMsg + "\". "
            + "Hãy trả lời ngắn gọn, thân thiện bằng tiếng Việt (1-3 câu).";

        AiHelper.chatReply(context, reply -> {
            removeTyping();
            // Lưu AI reply vào DB
            Message aiMsg = new Message();
            aiMsg.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
            aiMsg.conversationId = conversationId;
            aiMsg.senderId = other.userId; aiMsg.receiverId = me.userId;
            aiMsg.roomId = roomId; aiMsg.messageType = Message.TYPE_TEXT;
            aiMsg.content = reply; aiMsg.createdAt = System.currentTimeMillis();
            db.messageDao().insertMessage(aiMsg);

            Conversation c = db.messageDao().getConversation(conversationId);
            if (c != null) { c.lastMessage = reply; c.lastMessageAt = aiMsg.createdAt; db.messageDao().insertConversation(c); }

            loadMessages();
        });
    }

    // ─── Helpers ───

    private void addBubble(String senderId, String content) {
        Message m = new Message();
        m.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
        m.senderId = senderId; m.content = content; m.createdAt = System.currentTimeMillis();
        adapter.addMessage(m);
        rv.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void addTyping(String senderId) {
        Message t = new Message();
        t.messageId = "typing"; t.senderId = senderId;
        t.content = "⏳ Đang trả lời..."; t.createdAt = System.currentTimeMillis();
        adapter.addMessage(t);
        rv.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void removeTyping() {
        adapter.removeLast();
    }

    private void ensureConversation() {
        String sId = User.ROLE_STUDENT.equals(me.role) ? me.userId : other.userId;
        String lId = User.ROLE_LANDLORD.equals(me.role) ? me.userId : other.userId;
        conversationId = "conv-" + sId + "-" + lId + "-" + (roomId != null ? roomId : "x");
        Conversation c = db.messageDao().getConversation(conversationId);
        if (c == null) {
            c = new Conversation();
            c.conversationId = conversationId; c.studentId = sId; c.landlordId = lId;
            c.roomId = roomId;
            c.studentName = User.ROLE_STUDENT.equals(me.role) ? me.fullName : other.fullName;
            c.landlordName = User.ROLE_LANDLORD.equals(me.role) ? me.fullName : other.fullName;
            if (roomId != null) { Room r = db.roomDao().getById(roomId); if (r != null) c.roomTitle = r.title; }
            c.lastMessageAt = System.currentTimeMillis();
            db.messageDao().insertConversation(c);
        }
    }

    private void loadMessages() {
        List<Message> list = db.messageDao().getMessages(conversationId);
        adapter.setData(list);
        if (!list.isEmpty()) rv.scrollToPosition(list.size() - 1);
    }
}
