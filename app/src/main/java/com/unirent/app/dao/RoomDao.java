package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Room;
import java.util.List;

@Dao
public interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Room room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Room> rooms);

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("SELECT * FROM rooms WHERE roomId = :id LIMIT 1")
    Room getById(String id);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' ORDER BY createdAt DESC")
    List<Room> getAllApproved();

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'pending' ORDER BY createdAt DESC")
    List<Room> getPending();

    @Query("SELECT * FROM rooms WHERE landlordId = :landlordId ORDER BY createdAt DESC")
    List<Room> getByLandlord(String landlordId);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' AND price <= :maxPrice ORDER BY price ASC")
    List<Room> getByMaxPrice(long maxPrice);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' AND distanceToSchool <= :maxKm ORDER BY distanceToSchool ASC")
    List<Room> getByDistance(double maxKm);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' ORDER BY ratingAverage DESC LIMIT :n")
    List<Room> getTopRated(int n);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' ORDER BY price ASC LIMIT :n")
    List<Room> getCheapest(int n);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' ORDER BY createdAt DESC LIMIT :n")
    List<Room> getNewest(int n);

    @Query("SELECT * FROM rooms WHERE approvedStatus = 'approved' AND (title LIKE '%' || :q || '%' OR address LIKE '%' || :q || '%' OR schoolName LIKE '%' || :q || '%')")
    List<Room> search(String q);

    @Query("SELECT COUNT(*) FROM rooms")
    int count();

    @Query("SELECT COUNT(*) FROM rooms WHERE approvedStatus = 'approved'")
    int countApproved();
}
