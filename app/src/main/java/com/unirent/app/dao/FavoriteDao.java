package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Favorite;
import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite f);

    @Query("DELETE FROM favorites WHERE userId = :uid AND roomId = :rid")
    void delete(String uid, String rid);

    @Query("SELECT * FROM favorites WHERE userId = :uid")
    List<Favorite> getByUser(String uid);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :uid AND roomId = :rid)")
    boolean exists(String uid, String rid);

    @Query("SELECT roomId FROM favorites WHERE userId = :uid")
    List<String> getRoomIds(String uid);
}
