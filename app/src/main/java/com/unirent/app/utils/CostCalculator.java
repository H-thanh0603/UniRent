package com.unirent.app.utils;

import android.content.Context;
import com.unirent.app.models.Room;
import java.util.Locale;

/**
 * Dự toán tổng chi phí thật mỗi tháng cho sinh viên.
 * Không chỉ tiền phòng — còn điện, nước, wifi, rác, gửi xe, di chuyển.
 */
public class CostCalculator {

    // Đơn giá ước tính (có thể cấu hình sau)
    private static final int ELECTRIC_PER_KWH = 3500;   // đ/kWh
    private static final int WATER_PER_PERSON = 50000;   // đ/người/tháng
    private static final int WIFI_PER_PERSON = 50000;    // đ/người/tháng
    private static final int GARBAGE = 20000;            // đ/tháng
    private static final int PARKING = 80000;            // đ/xe/tháng
    private static final int LAUNDRY = 60000;            // đ/tháng (giặt đồ)
    private static final int GASOLINE_PER_KM = 250;      // đ/km (xe máy ~50km/lít, 25k/lít)
    private static final int BUS_FARE = 7000;            // đ/lượt (xe buýt SV)
    private static final int FOOD_ESTIMATE = 1500000;    // đ/tháng (cơ bản)

    // Lượng điện ước tính cho 1 người
    private static final int KWH_PER_PERSON = 70; // kWh/tháng (máy lạnh + quạt + đèn + sạc)

    public static class CostBreakdown {
        public long roomPrice;
        public long electric;
        public long water;
        public long wifi;
        public long garbage;
        public long parking;
        public long laundry;
        public long transport;
        public long food;
        public long totalFixed;    // tiền cố định (phòng + điện nước wifi rác gửi xe giặt)
        public long totalEstimate; // tổng tất cả

        public String toSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("💰 Tổng chi phí ước tính/tháng:\n\n");
            sb.append(String.format(Locale.US, "🏠 Tiền phòng:      %,dđ\n", roomPrice));
            sb.append(String.format(Locale.US, "⚡ Điện (~%d kWh):  %,dđ\n", KWH_PER_PERSON, electric));
            sb.append(String.format(Locale.US, "💧 Nước:            %,dđ\n", water));
            sb.append(String.format(Locale.US, "📶 Wifi:            %,dđ\n", wifi));
            sb.append(String.format(Locale.US, "🗑  Rác:             %,dđ\n", garbage));
            sb.append(String.format(Locale.US, "🏍  Gửi xe:          %,dđ\n", parking));
            sb.append(String.format(Locale.US, "👕 Giặt đồ:         %,dđ\n", laundry));
            sb.append(String.format(Locale.US, "🚌 Di chuyển:       %,dđ\n", transport));
            sb.append(String.format(Locale.US, "🍜 Ăn uống:         %,dđ\n", food));
            sb.append("──────────────────────────\n");
            sb.append(String.format(Locale.US, "📌 Tổng cố định:    %,dđ\n", totalFixed));
            sb.append(String.format(Locale.US, "💸 Tổng tất cả:     %,dđ\n", totalEstimate));
            return sb.toString();
        }

        public String toShortSummary() {
            return String.format(Locale.US, "💸 ~%,dđ/tháng (phòng %,dđ + chi phí khác %,dđ)",
                totalEstimate, roomPrice, totalFixed - roomPrice);
        }
    }

    public static CostBreakdown calculate(Room room, double distanceKm) {
        CostBreakdown cb = new CostBreakdown();
        cb.roomPrice = room.price;

        // Điện: nếu phòng có máy lạnh → dùng nhiều hơn
        boolean hasAC = room.amenities != null && room.amenities.contains("Máy lạnh");
        int kwh = hasAC ? 100 : 60;
        cb.electric = (long)(kwh * ELECTRIC_PER_KWH);

        // Nước
        cb.water = WATER_PER_PERSON;

        // Wifi (nếu phòng có)
        boolean hasWifi = room.amenities != null && room.amenities.contains("Wifi");
        cb.wifi = hasWifi ? 0 : WIFI_PER_PERSON; // nếu có wifi → miễn phí

        cb.garbage = GARBAGE;
        cb.parking = PARKING;
        cb.laundry = LAUNDRY;

        // Di chuyển: tính 2 lượt/ngày * 22 ngày học
        int schoolDays = 22;
        int tripsPerDay = 2;
        double kmPerMonth = distanceKm * tripsPerDay * schoolDays;
        cb.transport = (long)(kmPerMonth * GASOLINE_PER_KM);

        cb.food = FOOD_ESTIMATE;

        cb.totalFixed = cb.roomPrice + cb.electric + cb.water + cb.wifi
                      + cb.garbage + cb.parking + cb.laundry;
        cb.totalEstimate = cb.totalFixed + cb.transport + cb.food;

        return cb;
    }
}
