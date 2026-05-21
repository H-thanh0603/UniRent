# UniRent — Tìm phòng trọ thông minh cho sinh viên

> Đồ án Android Java — Smart Rent Finder for Students.
> 3 vai trò (Sinh viên / Chủ trọ / Admin), local SQLite (Room), AI helper, Material Design 3.

## ✨ Tính năng đã triển khai (MVP v1)

### 👨‍🎓 Sinh viên (Student)
- Đăng ký / đăng nhập theo vai trò
- Trang chủ với gợi ý AI (theo trường, ngân sách, khoảng cách)
- Tìm kiếm + lọc nhanh (giá < 2tr / 2-3tr / 3tr+, khoảng cách < 2km / 5km)
- Xem chi tiết phòng: ảnh, giá, tiện ích, mô tả, chi phí, quy định
- ⚠ Cảnh báo rủi ro AI (giá rẻ bất thường, mô tả sơ sài)
- ❤ Lưu phòng yêu thích, **so sánh đa phòng** với gợi ý AI
- 📅 Đặt lịch xem phòng (date + time picker)
- 💬 Chat trực tiếp với chủ trọ + câu hỏi gợi ý nhanh
- 🚩 Báo cáo phòng vi phạm

### 🏠 Chủ trọ (Landlord)
- Dashboard thống kê: tổng phòng / còn trống / lượt xem / lượt lưu / lịch hẹn
- Quản lý CRUD phòng (thêm / sửa)
- ✨ AI tự sinh mô tả phòng từ thông tin cơ bản
- Xác nhận / từ chối lịch hẹn
- Trạng thái phòng: chờ duyệt / đã duyệt / từ chối

### 🛡 Admin
- Duyệt / từ chối tin đăng phòng
- Quản lý danh sách người dùng (Student / Landlord / Admin)

## 🧰 Công nghệ

| Layer | Tech |
|---|---|
| Mobile | Android Java (minSdk 24, target 34) |
| UI | XML Layout + Material Components 3 |
| Local DB | Room (SQLite) |
| Image | Glide 4.16 |
| Layout flex | Google Flexbox |
| Build | Gradle 8.5 + AGP 8.2.0, Java 17 |
| Architecture | Activities + Fragments + Adapters + Repository pattern |

## 🗂 Cấu trúc project

```
app/src/main/java/com/unirent/app/
├── activities/      # Splash, Login, Register, Main, RoomDetail, AddRoom, Chat, Appointment, Compare
├── fragments/       # Home, Search, Favorite, Profile, LandlordDashboard/Rooms/Appointments, AdminPending/Users
├── adapters/        # RoomAdapter, MessageAdapter, AppointmentAdapter, ConversationAdapter, UserListAdapter
├── models/          # User, Room, Favorite, Appointment, Message, Conversation, Review, Report, Amenity
├── database/        # AppDatabase, Converters
├── dao/             # UserDao, RoomDao, FavoriteDao, AppointmentDao, MessageDao, ReviewDao, ReportDao
├── ai/              # AiHelper (gợi ý / mô tả / so sánh / cảnh báo)
└── utils/           # SeedData, SeedRoomsBatch1/2, SessionManager, FormatUtils
```


## 🚀 Cách build và chạy

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17
- Android SDK 34
- Thiết bị Android API 24+ hoặc emulator

### Bước build
1. Mở Android Studio → **File → Open** → chọn thư mục `UniRent`
2. Đợi Gradle sync xong (sẽ tự tải dependencies)
3. Run trên emulator hoặc thiết bị thật (`Shift+F10` hoặc nút ▶)

Nếu gradle wrapper chưa có:
```bash
gradle wrapper --gradle-version 8.5
```

## 🔑 Tài khoản demo

> Tất cả dùng password: **`123456`**

| Vai trò | Email |
|---------|-------|
| Sinh viên | `thanh@student.vn` (Nguyễn Đức Thanh) |
| Sinh viên | `mai@student.vn` (Trần Thị Mai) |
| Chủ trọ | `lan@landlord.vn` (Cô Lan) |
| Chủ trọ | `tuan@landlord.vn` (Anh Tuấn) |
| Admin | `admin@unirent.vn` |

→ Trên màn hình login có **3 nút quick login** để đỡ phải gõ.

## 📊 Dữ liệu mẫu

App seed sẵn **12 phòng trọ** quanh các trường ĐH HCM:
ĐH Công nghệ, Bách Khoa, KHTN, Kinh tế, Sư Phạm, Tôn Đức Thắng,
Văn Lang, ĐHQG, Mở, Hutech, FPT — đa dạng giá từ 900K đến 5.5tr/tháng.

Mỗi phòng có: ảnh thực (Unsplash), tiện ích, vị trí, đánh giá, chi phí, quy định.

## 🤖 Tính năng AI thông minh

| Tính năng | Mô tả |
|---|---|
| 🎯 Gợi ý phòng | Theo trường + ngân sách + sở thích |
| ✨ Sinh mô tả tự động | Chủ trọ nhập area + tiện ích → AI viết mô tả |
| ⚖ So sánh đa phòng | Phân tích ưu/nhược điểm, đưa lời khuyên |
| ⚠ Cảnh báo rủi ro | Phát hiện giá bất thường, mô tả sơ sài, ảnh thiếu |
| 💬 Câu hỏi gợi ý chat | 4 mẫu câu nhanh khi chat chủ trọ |

## 🛣 Roadmap (Version 2)

- [ ] Google Maps cho map view (cần API key)
- [ ] Firebase Authentication thay local DB
- [ ] Firebase Cloud Messaging push notification
- [ ] Upload ảnh phòng thực qua Firebase Storage
- [ ] Đánh giá phòng + comment
- [ ] Statistics dashboard với MPAndroidChart

## 📝 License

Đồ án sinh viên — sử dụng tự do cho mục đích học tập.
Tác giả: **Nguyễn Đức Thanh** ([@H-thanh0603](https://github.com/H-thanh0603))
