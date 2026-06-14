package com.unirent.app.adapters;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.models.Message;

import java.text.SimpleDateFormat;
import java.util.*;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
    private static final int OUT = 1, IN = 2, AI = 3;
    private final String myId;
    private final List<Message> data = new ArrayList<>();
    public MessageAdapter(String myId) { this.myId = myId; }

    public void setData(List<Message> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    public void addMessage(Message m) {
        data.add(m);
        notifyItemInserted(data.size() - 1);
    }

    public void removeLast() {
        if (!data.isEmpty()) {
            int idx = data.size() - 1;
            data.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    @Override public int getItemViewType(int p) {
        Message m = data.get(p);
        if ("ai".equals(m.senderId) || "typing".equals(m.messageId)) return AI;
        return m.senderId.equals(myId) ? OUT : IN;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        int layout = t == OUT ? R.layout.item_message_out
                   : t == AI ? R.layout.item_message_in
                   : R.layout.item_message_in;
        return new VH(LayoutInflater.from(p.getContext()).inflate(layout, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Message m = data.get(pos);
        h.tv.setText(m.content);
        h.time.setText(new SimpleDateFormat("HH:mm", Locale.US).format(new Date(m.createdAt)));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv, time;
        VH(@NonNull View v) { super(v); tv = v.findViewById(R.id.tv_msg); time = v.findViewById(R.id.tv_time); }
    }
}
