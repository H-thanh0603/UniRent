package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> users);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    User getById(String id);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :pwd LIMIT 1")
    User login(String email, String pwd);

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE role = :role")
    List<User> getByRole(String role);

    @Query("SELECT COUNT(*) FROM users")
    int count();
}
