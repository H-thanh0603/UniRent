package com.unirent.app.ai;

import com.unirent.app.models.Room;
import java.util.List;
import java.util.Locale;

public class AiHelper {

    public static String riskWarning(Room r) {
        if (r == null) return null;
        if (r.imageUrls == null || r.imageUrls.isEmpty()) {
            return "⚠ Phòng chưa có ảnh thực tế. Hãy liên hệ chủ trọ để xác nhận.";
        }
        if (r.description == null || r.description.length() < 30) {
            return "⚠ Mô tả phòng quá sơ sài. Bạn nên hỏi thêm trước khi đặt cọc.";
        }
        long avg = 2200000;
        double pct = (avg - r.price) * 100.0 / avg;
        if (r.price > 0 && pct > 35) {
            return String.format(Locale.US,
                "⚠ Giá thấp hơn mặt bằng khoảng %.0f%%. Hãy kiểm tra kỹ trước khi đặt cọc.", pct);
        }
        return null;
    }

    public static String generateDescription(int area, List<String> amenities, long price) {
        StringBuilder sb = new StringBuilder();
        sb.append("Phòng trọ rộng ").append(area).append("m², phù hợp cho sinh viên hoặc người đi làm.");
        if (amenities != null && !amenities.isEmpty()) {
            sb.append(" Phòng có ").append(String.join(", ", amenities)).append(".");
        }
        sb.append(" Không gian sạch sẽ, an ninh tốt, thuận tiện di chuyển đến trường và khu vực xung quanh.");
        sb.append(" Giá thuê chỉ ").append(String.format(Locale.US, "%,d", price)).append("đ/tháng.");
        return sb.toString();
    }

    public static String compareSummary(List<Room> rooms) {
        if (rooms == null || rooms.size() < 2) return "";
        Room cheap = rooms.get(0), spacious = rooms.get(0), best = rooms.get(0);
        for (Room r : rooms) {
            if (r.price < cheap.price) cheap = r;
            if (r.area > spacious.area) spacious = r;
            if (r.ratingAverage > best.ratingAverage) best = r;
        }
        StringBuilder sb = new StringBuilder("✨ Gợi ý từ AI:\n");
        sb.append("• Giá tốt nhất: ").append(cheap.title).append(".\n");
        sb.append("• Diện tích lớn nhất: ").append(spacious.title).append(".\n");
        sb.append("• Đánh giá cao nhất: ").append(best.title).append(".\n");
        sb.append("→ Nếu ưu tiên tiết kiệm, hãy chọn phòng đầu tiên. Nếu cần không gian rộng, ");
        sb.append("hãy ưu tiên phòng thứ hai. Nếu quan tâm trải nghiệm chung, phòng thứ ba phù hợp nhất.");
        return sb.toString();
    }

    public static String[] aiSuggestions(String role, String school) {
        if ("student".equals(role)) {
            return new String[]{
                "Phòng dưới 2 triệu " + (school != null && !school.isEmpty() ? "gần " + school : "gần trường"),
                "Phòng có máy lạnh, wifi, an ninh tốt",
                "Phòng cách trường dưới 2 km",
                "Studio cao cấp đánh giá 4.5★ trở lên"
            };
        }
        return new String[]{
            "Tạo tin đăng mới hấp dẫn",
            "Cập nhật trạng thái phòng",
            "Phản hồi tin nhắn nhanh",
            "Xem lịch hẹn xem phòng"
        };
    }
}
