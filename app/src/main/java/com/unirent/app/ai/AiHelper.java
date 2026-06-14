package com.unirent.app.ai;

import android.util.Log;
import com.unirent.app.models.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AI helper — gọi DeepSeek API cho các tác vụ thông minh.
 */
public class AiHelper {
    private static final String TAG = "UniRent-AI";

    // ─── Rule-based fallbacks (dùng khi API lỗi) ───

    public static String riskWarning(Room r) {
        if (r == null) return null;
        if (r.imageUrls == null || r.imageUrls.isEmpty())
            return "⚠ Phòng chưa có ảnh thực tế. Hãy liên hệ chủ trọ để xác nhận.";
        if (r.description == null || r.description.length() < 30)
            return "⚠ Mô tả phòng quá sơ sài. Bạn nên hỏi thêm trước khi đặt cọc.";
        long avg = 2200000;
        if (r.price > 0 && (avg - r.price) * 100.0 / avg > 35)
            return String.format(Locale.US, "⚠ Giá thấp hơn mặt bằng %.0f%%. Hãy kiểm tra kỹ trước khi đặt cọc.", (avg - r.price) * 100.0 / avg);
        return null;
    }

    public static String[] quickSuggestions(String role, String school) {
        if ("student".equals(role))
            return new String[]{"Phòng dưới 2 triệu " + (school != null && !school.isEmpty() ? "gần " + school : "gần trường"),
                "Phòng có máy lạnh, wifi, an ninh tốt", "Phòng cách trường dưới 2 km", "Studio cao cấp đánh giá 4.5★ trở lên"};
        return new String[]{"Tạo tin đăng mới hấp dẫn", "Cập nhật trạng thái phòng", "Phản hồi tin nhắn nhanh", "Xem lịch hẹn xem phòng"};
    }

    // ─── API-based AI features ───

    /** AI viết mô tả phòng từ thông tin cơ bản */
    public interface GenDescCallback { void onResult(String description); }
    public static void generateDescription(int area, List<String> amenities, long price, String school, GenDescCallback cb) {
        String prompt = String.format(Locale.US,
            "Viết một mô tả phòng trọ hấp dẫn bằng tiếng Việt (3-5 câu): diện tích %dm², giá %,dđ/tháng, tiện ích: %s, gần %s. Ngắn gọn, thu hút sinh viên.",
            area, price, amenities != null ? String.join(", ", amenities) : "cơ bản", school != null ? school : "trường đại học");

        ApiClient.chat("Bạn là trợ lý viết mô tả bất động sản chuyên nghiệp.", prompt, new ApiClient.Callback() {
            public void onSuccess(String r) { cb.onResult(r); }
            public void onError(String e) {
                Log.w(TAG, "generateDescription API error: " + e);
                cb.onResult(fallbackDesc(area, amenities, price));
            }
        });
    }

    private static String fallbackDesc(int area, List<String> amenities, long price) {
        StringBuilder sb = new StringBuilder();
        sb.append("Phòng trọ rộng ").append(area).append("m², phù hợp cho sinh viên.");
        if (amenities != null && !amenities.isEmpty()) sb.append(" Có ").append(String.join(", ", amenities)).append(".");
        sb.append(" Giá thuê ").append(String.format(Locale.US, "%,d", price)).append("đ/tháng.");
        return sb.toString();
    }

    /** AI phân tích rủi ro từ thông tin phòng */
    public interface RiskCallback { void onResult(String risk); }
    public static void analyzeRisk(Room room, RiskCallback cb) {
        String info = String.format(Locale.US,
            "Phân tích rủi ro phòng trọ sau: tiêu đề \"%s\", giá %,dđ, diện tích %dm², mô tả: \"%s\", có %d ảnh, đánh giá %.1f★ (%d lượt). Chỉ cảnh báo nếu có dấu hiệu đáng ngờ (giá quá thấp, mô tả sơ sài, không ảnh). Trả lời 1-2 câu ngắn gọn tiếng Việt.",
            room.title, room.price, room.area,
            room.description != null ? room.description : "không có",
            room.imageUrls != null ? room.imageUrls.size() : 0,
            room.ratingAverage, room.totalReviews);

        ApiClient.chat("Bạn là chuyên gia phân tích rủi ro thuê nhà.", info, new ApiClient.Callback() {
            public void onSuccess(String r) { cb.onResult("🤖 AI: " + r); }
            public void onError(String e) {
                Log.w(TAG, "analyzeRisk API error: " + e);
                String fallback = riskWarning(room);
                cb.onResult(fallback != null ? fallback : "✅ Không phát hiện rủi ro đáng kể.");
            }
        });
    }

    /** AI chat: trả lời câu hỏi về phòng trọ */
    public interface ChatCallback { void onResult(String reply); }
    public static void chatReply(String userMessage, ChatCallback cb) {
        String system = "Bạn là trợ lý tìm phòng trọ UniRent cho sinh viên Việt Nam. Trả lời ngắn gọn, hữu ích bằng tiếng Việt. "
            + "Bạn có thể tư vấn về: giá cả, vị trí, tiện ích, an ninh, kinh nghiệm thuê trọ, cảnh báo lừa đảo.";

        ApiClient.chat(system, userMessage, new ApiClient.Callback() {
            public void onSuccess(String r) { cb.onResult(r); }
            public void onError(String e) {
                Log.w(TAG, "chatReply API error: " + e);
                cb.onResult("Xin lỗi, trợ lý AI đang bận. Bạn hãy thử lại sau nhé!");
            }
        });
    }

    /** AI tìm phòng: parse ngôn ngữ tự nhiên thành tiêu chí */
    public interface SearchCallback { void onResult(String filterSummary); }
    public static void parseSearch(String query, SearchCallback cb) {
        String prompt = "Người dùng muốn tìm phòng trọ với yêu cầu: \"" + query + "\". "
            + "Hãy tóm tắt các tiêu chí tìm kiếm thành 1 câu ngắn gọn tiếng Việt.";

        ApiClient.chat("Bạn là trợ lý tìm kiếm bất động sản.", prompt, new ApiClient.Callback() {
            public void onSuccess(String r) { cb.onResult("🔍 " + r); }
            public void onError(String e) {
                Log.w(TAG, "parseSearch API error: " + e);
                cb.onResult("🔍 Đang tìm: " + query);
            }
        });
    }

    // ─── Sync fallback (giữ tương thích code cũ) ───

    public static String generateDescription(int area, List<String> amenities, long price) {
        return fallbackDesc(area, amenities, price);
    }

    public static String[] aiSuggestions(String role, String school) {
        return quickSuggestions(role, school);
    }

    public static String compareSummary(List<Room> rooms) {
        if (rooms == null || rooms.size() < 2) return "";
        Room cheap = rooms.get(0), spacious = rooms.get(0), best = rooms.get(0);
        for (Room r : rooms) {
            if (r.price < cheap.price) cheap = r;
            if (r.area > spacious.area) spacious = r;
            if (r.ratingAverage > best.ratingAverage) best = r;
        }
        StringBuilder sb = new StringBuilder("✨ Gợi ý:\n");
        sb.append("• Giá tốt nhất: ").append(cheap.title).append("\n");
        sb.append("• Diện tích lớn nhất: ").append(spacious.title).append("\n");
        sb.append("• Đánh giá cao nhất: ").append(best.title).append("\n");
        sb.append("→ Ưu tiên ngân sách: phòng 1. Cần rộng: phòng 2. Trải nghiệm: phòng 3.");
        return sb.toString();
    }

    // ─── AI Price Prediction ───

    public interface PriceCallback { void onResult(String prediction); }
    public static void predictPrice(int area, String location, List<String> amenities, PriceCallback cb) {
        String prompt = String.format(Locale.US,
            "Dự đoán giá thuê phòng trọ hợp lý cho sinh viên (VNĐ/tháng): diện tích %dm², khu vực \"%s\", tiện ích: %s. "
            + "Chỉ trả lời: \"Khoảng X - Y đồng/tháng\" và 1 câu giải thích ngắn.",
            area, location != null ? location : "gần trường đại học",
            amenities != null ? String.join(", ", amenities) : "cơ bản");

        ApiClient.chat("Bạn là chuyên gia bất động sản cho thuê.", prompt, new ApiClient.Callback() {
            public void onSuccess(String r) { cb.onResult("🤖 " + r); }
            public void onError(String e) {
                long est = area * 80000L;
                cb.onResult(String.format(Locale.US, "💰 Ước tính: %,d - %,dđ/tháng (dựa trên diện tích %dm²)", est, est + 1000000, area));
            }
        });
    }
}
