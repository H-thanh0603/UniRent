package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Roommate;
import java.util.List;

@Dao
public interface RoommateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Roommate r);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Roommate> list);

    @Query("SELECT * FROM roommates WHERE userId != :excludeUserId ORDER BY createdAt DESC")
    List<Roommate> getAllExcept(String excludeUserId);

    @Query("SELECT * FROM roommates WHERE userId = :userId LIMIT 1")
    Roommate getByUserId(String userId);

    @Query("SELECT COUNT(*) FROM roommates")
    int count();
}
