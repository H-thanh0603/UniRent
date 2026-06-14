package com.unirent.app.utils;

import java.util.Locale;

/**
 * Ước tính thời gian di chuyển thực tế.
 * Dựa trên khoảng cách và phương tiện.
 */
public class TravelTime {

    public enum Mode { MOTORBIKE, BUS, WALKING }

    // Tốc độ trung bình (đã tính kẹt xe nhẹ):
    // Xe máy: 25 km/h (thực tế trong thành phố, tính cả đèn đỏ)
    // Xe buýt: 18 km/h (tính cả dừng trạm)
    // Đi bộ: 5 km/h
    private static final double SPEED_MOTORBIKE = 25.0;
    private static final double SPEED_BUS = 18.0;
    private static final double SPEED_WALKING = 5.0;

    public static class TimeEstimate {
        public int motorbikeMinutes;
        public int busMinutes;
        public int walkingMinutes;
        public double distanceKm;

        public String toSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("⏱ Thời gian đến trường:\n");
            sb.append(String.format(Locale.US, "  🏍  Xe máy:  ~%d phút\n", motorbikeMinutes));
            sb.append(String.format(Locale.US, "  🚌 Xe buýt:  ~%d phút\n", busMinutes));
            sb.append(String.format(Locale.US, "  🚶 Đi bộ:    ~%d phút\n", walkingMinutes));
            sb.append(String.format(Locale.US, "  📍 Khoảng cách: %.1f km\n", distanceKm));
            return sb.toString();
        }

        public String toShort() {
            return String.format(Locale.US, "🏍~%dph | 🚌~%dph | 🚶~%dph (%.1fkm)",
                motorbikeMinutes, busMinutes, walkingMinutes, distanceKm);
        }
    }

    public static TimeEstimate calculate(double distanceKm) {
        TimeEstimate t = new TimeEstimate();
        t.distanceKm = distanceKm;
        t.motorbikeMinutes = (int) Math.round((distanceKm / SPEED_MOTORBIKE) * 60);
        t.busMinutes = (int) Math.round((distanceKm / SPEED_BUS) * 60);
        t.walkingMinutes = (int) Math.round((distanceKm / SPEED_WALKING) * 60);
        return t;
    }

    public static String getBestArea(String schoolName) {
        if (schoolName == null) return "";
        String s = schoolName.toLowerCase(Locale.ROOT);
        if (s.contains("hutech") || s.contains("công nghệ")) return "Bình Thạnh, Quận 9, Thủ Đức";
        if (s.contains("ute") || s.contains("sư phạm kỹ thuật")) return "Thủ Đức, Quận 9";
        if (s.contains("văn lang")) return "Bình Thạnh, Gò Vấp, Quận 3";
        if (s.contains("bách khoa")) return "Quận 10, Quận 3, Phú Nhuận";
        if (s.contains("tôn đức thắng")) return "Quận 7, Nhà Bè";
        if (s.contains("kinh tế")) return "Bình Thạnh, Quận 3, Phú Nhuận";
        if (s.contains("iuh") || s.contains("công nghiệp")) return "Gò Vấp, Quận 12";
        if (s.contains("sư phạm")) return "Quận 5, Quận 10";
        if (s.contains("khoa học tự nhiên") || s.contains("khxh")) return "Quận 5, Quận 1, Quận 3";
        return "Khu vực gần trường";
    }
}
