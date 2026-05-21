package com.unirent.app.dao;

import androidx.room.*;
import com.unirent.app.models.Appointment;
import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment a);

    @Update
    void update(Appointment a);

    @Query("SELECT * FROM appointments WHERE studentId = :uid ORDER BY appointmentTime DESC")
    List<Appointment> getByStudent(String uid);

    @Query("SELECT * FROM appointments WHERE landlordId = :uid ORDER BY appointmentTime DESC")
    List<Appointment> getByLandlord(String uid);

    @Query("SELECT * FROM appointments WHERE appointmentId = :id LIMIT 1")
    Appointment getById(String id);
}
