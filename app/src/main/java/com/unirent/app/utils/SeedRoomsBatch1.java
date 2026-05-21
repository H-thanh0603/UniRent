package com.unirent.app.utils;

import com.unirent.app.models.Room;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SeedRoomsBatch1 {
    static List<String> a(String... s) { return new ArrayList<>(Arrays.asList(s)); }

    public static List<Room> list(long now) {
        List<Room> r = new ArrayList<>();

        r.add(SeedRooms.build("room-001", "land-1",
            "Phòng trọ sinh viên gần ĐH Công nghệ",
            "123 Nguyễn Văn Cừ, Q.5, TP.HCM",
            2000000, 20, 1.2, "Đại học Công nghệ TP.HCM",
            10.7626, 106.6829, 4.6f, 24,
            a("Wifi","Máy lạnh","Toilet riêng","Gác lửng","Chỗ để xe","Camera"),
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
            now));

        r.add(SeedRooms.build("room-002", "land-1",
            "Căn hộ mini full nội thất",
            "45 Lê Văn Sỹ, Q.3, TP.HCM",
            3500000, 25, 2.5, "Đại học Bách Khoa",
            10.7857, 106.6712, 4.8f, 41,
            a("Wifi","Máy lạnh","Bếp","Tủ lạnh","Máy giặt","Toilet riêng","Ban công"),
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
            now));

        r.add(SeedRooms.build("room-003", "land-2",
            "Phòng giá rẻ cho sinh viên ĐH KHTN",
            "78 Tô Ký, Q.12, TP.HCM",
            1500000, 16, 0.8, "Đại học Khoa học Tự nhiên",
            10.8543, 106.6253, 4.2f, 12,
            a("Wifi","Toilet riêng","Chỗ để xe"),
            "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800",
            now));

        r.add(SeedRooms.build("room-004", "land-2",
            "Phòng cao cấp gần ĐH Kinh tế",
            "210 Nguyễn Tri Phương, Q.10",
            4200000, 28, 1.5, "Đại học Kinh tế TP.HCM",
            10.7711, 106.6671, 4.9f, 67,
            a("Wifi","Máy lạnh","Bếp","Tủ lạnh","Toilet riêng","Cổng riêng","Camera","Khóa vân tay"),
            "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800",
            now));

        r.add(SeedRooms.build("room-005", "land-1",
            "Phòng có gác lửng giá tốt",
            "56 Đinh Tiên Hoàng, Bình Thạnh",
            1900000, 18, 2.0, "Đại học Công nghệ TP.HCM",
            10.8003, 106.7012, 4.3f, 18,
            a("Wifi","Gác lửng","Toilet riêng","Chỗ để xe","Bếp"),
            "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800",
            now));

        r.add(SeedRooms.build("room-006", "land-2",
            "Studio đẹp gần ĐH Sư Phạm",
            "100 Lê Văn Việt, Q.9",
            2800000, 22, 1.0, "Đại học Sư Phạm",
            10.8455, 106.7822, 4.7f, 33,
            a("Wifi","Máy lạnh","Toilet riêng","Bếp","Tủ lạnh","Ban công"),
            "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800",
            now));

        return r;
    }
}
