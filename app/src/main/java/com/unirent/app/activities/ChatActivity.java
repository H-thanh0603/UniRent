package com.unirent.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.adapters.MessageAdapter;
import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.*;
import com.unirent.app.utils.SessionManager;

import java.util.*;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_OTHER_ID = "other_id";

    private AppDatabase db;
    private User me, other;
    private String conversationId;
    private String roomId;
    private MessageAdapter adapter;
    private RecyclerView rv;
    private EditText etInput;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_chat);
        db = AppDatabase.getInstance(this);
        me = new SessionManager(this).getCurrentUser();
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        String oid = getIntent().getStringExtra(EXTRA_OTHER_ID);
        other = db.userDao().getById(oid);
        if (me == null || other == null) { finish(); return; }

        ((TextView)findViewById(R.id.tv_other)).setText(other.fullName);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rv = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(me.userId);
        rv.setAdapter(adapter);

        ensureConversation();
        loadMessages();

        findViewById(R.id.btn_send).setOnClickListener(v -> send());

        // Quick reply suggestions
        String[] quick = {"Phòng còn trống không ạ?", "Giá đã bao gồm điện nước chưa?",
                          "Có được nấu ăn không?", "Chiều nay xem phòng được không?"};
        LinearLayout chips = findViewById(R.id.ll_chips);
        for (String q : quick) {
            TextView c = new TextView(this);
            c.setText(q);
            c.setBackgroundResource(R.drawable.bg_chip_blue);
            c.setTextColor(getResources().getColor(R.color.badge_blue_text));
            c.setPadding(24, 12, 24, 12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 12, 0);
            c.setLayoutParams(lp);
            c.setOnClickListener(v -> { etInput.setText(q); etInput.setSelection(q.length()); });
            chips.addView(c);
        }
    }

    private void ensureConversation() {
        String studentId = User.ROLE_STUDENT.equals(me.role) ? me.userId : other.userId;
        String landlordId = User.ROLE_LANDLORD.equals(me.role) ? me.userId : other.userId;
        conversationId = "conv-" + studentId + "-" + landlordId + "-" + (roomId == null ? "x" : roomId);
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

    private void send() {
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
