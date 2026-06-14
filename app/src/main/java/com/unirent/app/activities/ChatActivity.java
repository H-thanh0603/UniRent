package com.unirent.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private String conversationId;
    private String roomId;
    private MessageAdapter adapter;
    private RecyclerView rv;
    private EditText etInput;
    private boolean isAiMode;
    private String aiName = "UniRent AI";

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_chat);
        db = AppDatabase.getInstance(this);
        me = new SessionManager(this).getCurrentUser();
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        isAiMode = getIntent().getBooleanExtra(EXTRA_AI_MODE, false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rv = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(me != null ? me.userId : "user");
        rv.setAdapter(adapter);

        if (isAiMode) {
            setupAiChat(toolbar);
        } else {
            setupUserChat(toolbar);
        }
    }

    private void setupAiChat(MaterialToolbar toolbar) {
        toolbar.setTitle(aiName);
        findViewById(R.id.hsv_quick).setVisibility(View.GONE);

        // Welcome message
        Message welcome = new Message();
        welcome.messageId = "ai-intro";
        welcome.senderId = "ai";
        welcome.content = "👋 Xin chào! Tôi là trợ lý AI của UniRent. Tôi có thể giúp bạn:\n"
            + "• Tư vấn tìm phòng trọ phù hợp\n"
            + "• Giải đáp thắc mắc về giá cả, tiện ích\n"
            + "• Cảnh báo rủi ro khi thuê trọ\n\n"
            + "Bạn cần giúp gì ạ?";
        welcome.createdAt = System.currentTimeMillis();
        List<Message> init = new ArrayList<>();
        init.add(welcome);
        adapter.setData(init);

        findViewById(R.id.btn_send).setOnClickListener(v -> sendAiMessage());
    }

    private void setupUserChat(MaterialToolbar toolbar) {
        String oid = getIntent().getStringExtra(EXTRA_OTHER_ID);
        other = db.userDao().getById(oid);
        if (me == null || other == null) {
            Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        toolbar.setTitle(other.fullName);

        ensureConversation();
        loadMessages();
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());

        // Quick reply chips
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
            lp.setMargins(0, 0, 10, 0);
            c.setLayoutParams(lp);
            c.setOnClickListener(v -> { etInput.setText(q); etInput.setSelection(q.length()); });
            chips.addView(c);
        }
    }

    private void sendAiMessage() {
        String txt = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(txt)) return;

        Message userMsg = new Message();
        userMsg.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
        userMsg.senderId = me != null ? me.userId : "user";
        userMsg.content = txt;
        userMsg.createdAt = System.currentTimeMillis();
        adapter.addMessage(userMsg);
        rv.scrollToPosition(adapter.getItemCount() - 1);
        etInput.setText("");

        // Typing indicator
        Message typing = new Message();
        typing.messageId = "typing";
        typing.senderId = "ai";
        typing.content = "⏳ Đang suy nghĩ...";
        typing.createdAt = System.currentTimeMillis();
        adapter.addMessage(typing);

        AiHelper.chatReply(txt, reply -> {
            adapter.removeLast();
            Message aiMsg = new Message();
            aiMsg.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
            aiMsg.senderId = "ai";
            aiMsg.content = reply;
            aiMsg.createdAt = System.currentTimeMillis();
            adapter.addMessage(aiMsg);
            rv.scrollToPosition(adapter.getItemCount() - 1);
        });
    }

    private void ensureConversation() {
        String studentId = User.ROLE_STUDENT.equals(me.role) ? me.userId : other.userId;
        String landlordId = User.ROLE_LANDLORD.equals(me.role) ? me.userId : other.userId;
        conversationId = "conv-" + studentId + "-" + landlordId + "-" + (roomId != null ? roomId : "x");
        Conversation c = db.messageDao().getConversation(conversationId);
        if (c == null) {
            c = new Conversation();
            c.conversationId = conversationId;
            c.studentId = studentId;
            c.landlordId = landlordId;
            c.roomId = roomId;
            c.studentName = User.ROLE_STUDENT.equals(me.role) ? me.fullName : other.fullName;
            c.landlordName = User.ROLE_LANDLORD.equals(me.role) ? me.fullName : other.fullName;
            if (roomId != null) {
                Room r = db.roomDao().getById(roomId);
                if (r != null) c.roomTitle = r.title;
            }
            c.lastMessageAt = System.currentTimeMillis();
            db.messageDao().insertConversation(c);
        }
    }

    private void loadMessages() {
        List<Message> list = db.messageDao().getMessages(conversationId);
        adapter.setData(list);
        if (!list.isEmpty()) rv.scrollToPosition(list.size() - 1);
    }

    private void sendMessage() {
        String txt = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(txt)) return;
        Message m = new Message();
        m.messageId = "msg-" + UUID.randomUUID().toString().substring(0,8);
        m.conversationId = conversationId;
        m.senderId = me.userId;
        m.receiverId = other.userId;
        m.roomId = roomId;
        m.messageType = Message.TYPE_TEXT;
        m.content = txt;
        m.createdAt = System.currentTimeMillis();
        db.messageDao().insertMessage(m);

        Conversation c = db.messageDao().getConversation(conversationId);
        if (c != null) {
            c.lastMessage = txt;
            c.lastMessageAt = m.createdAt;
            db.messageDao().insertConversation(c);
        }

        etInput.setText("");
        loadMessages();
    }
}
