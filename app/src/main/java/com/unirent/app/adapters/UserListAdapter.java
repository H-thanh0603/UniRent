package com.unirent.app.adapters;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.models.User;

import java.util.*;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.VH> {
    private final List<User> data = new ArrayList<>();
    public void setData(List<User> list) {
        data.clear(); if (list != null) data.addAll(list); notifyDataSetChanged();
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_user, p, false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        User u = data.get(pos);
        h.name.setText(u.fullName);
        h.email.setText(u.email);
        h.role.setText(label(u.role));
    }
    private String label(String role) {
        switch (role) {
            case User.ROLE_STUDENT: return "Sinh viên";
            case User.ROLE_LANDLORD: return "Chủ trọ";
            case User.ROLE_ADMIN: return "Admin";
            default: return role;
        }
    }
    @Override public int getItemCount() { return data.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, role;
        VH(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tv_name);
            email = v.findViewById(R.id.tv_email);
            role = v.findViewById(R.id.tv_role);
        }
    }
}
