package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Report;
import java.util.List;

@Dao
public interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Report r);

    @Update
    void update(Report r);

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    List<Report> getAll();

    @Query("SELECT * FROM reports WHERE status = 'pending' ORDER BY createdAt DESC")
    List<Report> getPending();
}
