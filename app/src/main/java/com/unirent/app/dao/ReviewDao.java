package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Review r);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Review> list);

    @Query("SELECT * FROM reviews WHERE roomId = :rid ORDER BY createdAt DESC")
    List<Review> getByRoom(String rid);

    @Query("SELECT AVG(rating) FROM reviews WHERE roomId = :rid")
    Float averageOf(String rid);

    @Query("SELECT COUNT(*) FROM reviews WHERE roomId = :rid")
    int countOf(String rid);
}
