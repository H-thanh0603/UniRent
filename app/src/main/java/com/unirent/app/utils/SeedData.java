package com.unirent.app.utils;

import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.User;
import java.util.UUID;

public class SeedData {
    public static void seed(AppDatabase db) {
        long now = System.currentTimeMillis();

        User admin = new User();
        admin.userId = "admin-1";
        admin.fullName = "Quản trị viên";
        admin.email = "admin@unirent.vn";
        admin.password = "123456";
        admin.role = User.ROLE_ADMIN;
        admin.phone = "0900000000";
        admin.createdAt = now;
        admin.status = "active";
        db.userDao().insert(admin);

        User student = new User();
        student.userId = "user-thanh";
        student.fullName = "Nguyễn Đức Thanh";
        student.email = "thanh@student.vn";
        student.password = "123456";
        student.role = User.ROLE_STUDENT;
        student.phone = "0901234567";
        student.schoolName = "Đại học Công nghệ TP.HCM";
        student.createdAt = now;
        student.status = "active";
        db.userDao().insert(student);

        User student2 = new User();
        student2.userId = "user-mai";
        student2.fullName = "Trần Thị Mai";
        student2.email = "mai@student.vn";
        student2.password = "123456";
        student2.role = User.ROLE_STUDENT;
        student2.phone = "0902345678";
        student2.schoolName = "Đại học Bách Khoa";
        student2.createdAt = now;
        student2.status = "active";
        db.userDao().insert(student2);

        User landlord = new User();
        landlord.userId = "land-1";
        landlord.fullName = "Cô Lan (Chủ trọ A)";
        landlord.email = "lan@landlord.vn";
        landlord.password = "123456";
        landlord.role = User.ROLE_LANDLORD;
        landlord.phone = "0911111111";
        landlord.createdAt = now;
        landlord.status = "active";
        db.userDao().insert(landlord);

        User landlord2 = new User();
        landlord2.userId = "land-2";
        landlord2.fullName = "Anh Tuấn (Chủ trọ B)";
        landlord2.email = "tuan@landlord.vn";
        landlord2.password = "123456";
        landlord2.role = User.ROLE_LANDLORD;
        landlord2.phone = "0922222222";
        landlord2.createdAt = now;
        landlord2.status = "active";
        db.userDao().insert(landlord2);

        SeedRooms.seedRooms(db, now);
    }
}
