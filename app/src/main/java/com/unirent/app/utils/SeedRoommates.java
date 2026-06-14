package com.unirent.app.utils;

import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Roommate;

public class SeedRoommates {
    public static void seed(AppDatabase db) {
        if (db.roommateDao().count() > 0) return;
        long now = System.currentTimeMillis();

        Roommate r1 = new Roommate();
        r1.profileId = "rm-1"; r1.userId = "user-thanh"; r1.fullName = "Nguyễn Đức Thanh";
        r1.age = 21; r1.gender = "Nam"; r1.schoolName = "ĐH Công nghệ TP.HCM";
        r1.budgetMin = 1500000; r1.budgetMax = 2500000; r1.preferredArea = "Quận 9, Thủ Đức";
        r1.lifestyle = "Sáng tạo"; r1.sleepSchedule = "Cú đêm"; r1.cleanliness = "Sạch sẽ";
        r1.bio = "Sinh viên IT năm 3, thích yên tĩnh buổi tối, không hút thuốc."; r1.avatarEmoji = "🧑‍💻"; r1.createdAt = now;
        db.roommateDao().insert(r1);

        Roommate r2 = new Roommate();
        r2.profileId = "rm-2"; r2.userId = "user-mai"; r2.fullName = "Trần Thị Mai";
        r2.age = 20; r2.gender = "Nữ"; r2.schoolName = "ĐH Bách Khoa";
        r2.budgetMin = 2000000; r2.budgetMax = 3000000; r2.preferredArea = "Quận 10";
        r2.lifestyle = "Yên tĩnh"; r2.sleepSchedule = "Bình thường"; r2.cleanliness = "Sạch sẽ";
        r2.bio = "Sinh viên năm 2, cần bạn cùng phòng nữ, ưu tiên gần trường."; r2.avatarEmoji = "👩‍🎓"; r2.createdAt = now;
        db.roommateDao().insert(r2);

        Roommate r3 = new Roommate();
        r3.profileId = "rm-3"; r3.userId = "rm-user-3"; r3.fullName = "Lê Văn Hùng";
        r3.age = 22; r3.gender = "Nam"; r3.schoolName = "ĐH Kinh tế";
        r3.budgetMin = 1000000; r3.budgetMax = 2000000; r3.preferredArea = "Bình Thạnh";
        r3.lifestyle = "Năng động"; r3.sleepSchedule = "Sớm"; r3.cleanliness = "Bình thường";
        r3.bio = "Thích thể thao, chạy bộ mỗi sáng, tìm bạn cùng phòng năng động."; r3.avatarEmoji = "🏃"; r3.createdAt = now;
        db.roommateDao().insert(r3);

        Roommate r4 = new Roommate();
        r4.profileId = "rm-4"; r4.userId = "rm-user-4"; r4.fullName = "Phạm Thị Hương";
        r4.age = 19; r4.gender = "Nữ"; r4.schoolName = "ĐH KHXH&NV";
        r4.budgetMin = 1800000; r4.budgetMax = 2800000; r4.preferredArea = "Quận 1, Quận 3";
        r4.lifestyle = "Sáng tạo"; r4.sleepSchedule = "Cú đêm"; r4.cleanliness = "Sạch sẽ";
        r4.bio = "Sinh viên thiết kế, cần không gian sáng tạo chung, thích vẽ và decor."; r4.avatarEmoji = "🎨"; r4.createdAt = now;
        db.roommateDao().insert(r4);

        Roommate r5 = new Roommate();
        r5.profileId = "rm-5"; r5.userId = "rm-user-5"; r5.fullName = "Ngô Quốc Bảo";
        r5.age = 23; r5.gender = "Nam"; r5.schoolName = "ĐH Sư phạm Kỹ thuật";
        r5.budgetMin = 1200000; r5.budgetMax = 1800000; r5.preferredArea = "Thủ Đức";
        r5.lifestyle = "Năng động"; r5.sleepSchedule = "Bình thường"; r5.cleanliness = "Thoải mái";
        r5.bio = "Tìm bạn cùng phòng để chia sẻ tiền trọ, thích nấu ăn chung."; r5.avatarEmoji = "🍳"; r5.createdAt = now;
        db.roommateDao().insert(r5);
    }
}
