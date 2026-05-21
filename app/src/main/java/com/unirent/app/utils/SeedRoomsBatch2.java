package com.unirent.app.utils;

import com.unirent.app.models.Room;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SeedRoomsBatch2 {
    static List<String> a(String... s) { return new ArrayList<>(Arrays.asList(s)); }

    public static List<Room> list(long now) {
        List<Room> r = new ArrayList<>();

        r.add(SeedRooms.build("room-007", "land-1",
            "Phòng ghép 4 người siêu rẻ",
            "32 Hoàng Diệu, Q.4",
            900000, 30, 1.8, "Đại học Tôn Đức Thắng",
            10.7625, 106.7012, 3.9f, 8,
            a("Wifi","Toilet chung","Chỗ để xe"),
            "https://images.unsplash.com/photo-1540518614846-7eded433c457?w=800",
            now));

        r.add(SeedRooms.build("room-008", "land-2",
            "Phòng có ban công thoáng mát",
            "88 Phan Văn Trị, Gò Vấp",
            2300000, 22, 3.0, "Đại học Văn Lang",
            10.8266, 106.6789, 4.5f, 27,
            a("Wifi","Máy lạnh","Toilet riêng","Ban công","Chỗ để xe"),
            "https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?w=800",
            now));

        r.add(SeedRooms.build("room-009", "land-1",
            "Trọ KTX kiểu mới gần ĐH Quốc gia",
            "12 Hàn Thuyên, Linh Trung, Thủ Đức",
            1700000, 18, 0.5, "ĐHQG TP.HCM",
            10.8703, 106.8013, 4.4f, 36,
            a("Wifi","Máy lạnh","Toilet riêng","Bảo vệ","Camera"),
            "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800",
            now));

        r.add(SeedRooms.build("room-010", "land-2",
            "Studio cao cấp khu trung tâm",
            "8 Nguyễn Đình Chiểu, Q.1",
            5500000, 30, 4.5, "Đại học Mở TP.HCM",
            10.7770, 106.6911, 4.9f, 89,
            a("Wifi","Máy lạnh","Bếp","Tủ lạnh","Máy giặt","Toilet riêng","Ban công","Cổng riêng"),
            "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800",
            now));

        r.add(SeedRooms.build("room-011", "land-1",
            "Phòng giá tốt cho cặp sinh viên",
            "55 Trường Chinh, Tân Bình",
            1800000, 20, 2.2, "Đại học Hutech",
            10.8033, 106.6432, 4.1f, 15,
            a("Wifi","Máy lạnh","Toilet riêng","Bếp"),
            "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800",
            now));

        r.add(SeedRooms.build("room-012", "land-2",
            "Phòng mới xây gần ĐH FPT",
            "21 D1, Khu CNC, Q.9",
            2600000, 24, 1.3, "Đại học FPT",
            10.8403, 106.8112, 4.7f, 22,
            a("Wifi","Máy lạnh","Toilet riêng","Bếp","Camera","Khóa vân tay"),
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            now));

        return r;
    }
}
