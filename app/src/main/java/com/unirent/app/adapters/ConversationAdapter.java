package com.unirent.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.activities.ChatActivity;
import com.unirent.app.models.Conversation;
import com.unirent.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {
    private final List<Conversation> data = new ArrayList<>();
    private final String myId;

    public ConversationAdapter(String myId) { this.myId = myId; }

    public void setData(List<Conversation> list) {
        data.clear(); if (list != null) data.addAll(list); notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_conversation, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Conversation c = data.get(pos);
        boolean iAmStudent = myId.equals(c.studentId);
        String otherName = iAmStudent ? c.landlordName : c.studentName;
        String otherId = iAmStudent ? c.landlordId : c.studentId;
        h.name.setText(otherName != null ? otherName : "Người dùng");
        h.last.setText(c.lastMessage != null ? c.lastMessage : "(Chưa có tin nhắn)");
        h.time.setText(new SimpleDateFormat("HH:mm dd/MM", Locale.US).format(new Date(c.lastMessageAt)));
        h.room.setText(c.roomTitle != null ? c.roomTitle : "");
        h.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent i = new Intent(ctx, ChatActivity.class);
            i.putExtra(ChatActivity.EXTRA_OTHER_ID, otherId);
            i.putExtra(ChatActivity.EXTRA_ROOM_ID, c.roomId);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, last, time, room;
        VH(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tv_name);
            last = v.findViewById(R.id.tv_last);
            time = v.findViewById(R.id.tv_time);
            room = v.findViewById(R.id.tv_room);
        }
    }
}
