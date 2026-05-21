package com.unirent.app.utils;

import com.unirent.app.database.AppDatabase;
import com.unirent.app.models.Room;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SeedRooms {

    static Room build(String id, String landlordId, String title, String addr, long price,
                     int area, double dist, String school, double lat, double lng,
                     float rating, int reviews, List<String> amenities, String cover, long now) {
        Room r = new Room();
        r.roomId = id;
        r.landlordId = landlordId;
        r.title = title;
        r.address = addr;
        r.price = price;
        r.deposit = price;
        r.area = area;
        r.distanceToSchool = dist;
        r.schoolName = school;
        r.latitude = lat;
        r.longitude = lng;
        r.ratingAverage = rating;
        r.totalReviews = reviews;
        r.amenities = amenities;
        r.coverImageUrl = cover;
        r.imageUrls = Arrays.asList(cover);
        r.status = Room.STATUS_AVAILABLE;
        r.approvedStatus = Room.APPROVED;
        r.roomType = "Phòng đơn";
        r.maxPeople = 2;
        r.electricFee = 3500;
        r.waterFee = 80000;
        r.rules = "Giờ giấc tự do. Tối đa 2 người. Không nuôi thú cưng.";
        r.description = "Phòng sạch sẽ, an ninh tốt, gần trường, thuận tiện di chuyển. " +
                        "Có đầy đủ tiện nghi cơ bản, phù hợp với sinh viên hoặc người đi làm.";
        r.viewCount = (int)(Math.random() * 500) + 50;
        r.favoriteCount = (int)(Math.random() * 80) + 5;
        r.createdAt = now - (long)(Math.random() * 30L * 24 * 3600 * 1000);
        r.updatedAt = r.createdAt;
        return r;
    }

    public static void seedRooms(AppDatabase db, long now) {
        List<Room> rooms = new ArrayList<>();
        rooms.addAll(SeedRoomsBatch1.list(now));
        rooms.addAll(SeedRoomsBatch2.list(now));
        db.roomDao().insertAll(rooms);
    }
}
