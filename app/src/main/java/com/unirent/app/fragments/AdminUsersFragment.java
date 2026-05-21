package com.unirent.app.fragments;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unirent.app.R;
import com.unirent.app.adapters.UserListAdapter;
import com.unirent.app.database.AppDatabase;

public class AdminUsersFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_admin_users, parent, false);
        AppDatabase db = AppDatabase.getInstance(requireContext());

        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        UserListAdapter adapter = new UserListAdapter();
        adapter.setData(db.userDao().getAll());
        rv.setAdapter(adapter);
        return root;
    }
}
