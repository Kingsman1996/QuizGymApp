# 🧠 QuizGymApp – Hệ thống thi trắc nghiệm online

QuizGymApp là một nền tảng thi trắc nghiệm trực tuyến, hỗ trợ:
- Thi theo phòng real-time bằng WebSocket
- Đăng nhập bằng JWT hoặc Google OAuth2
- Quản lý bài thi, chấm điểm, xếp hạng
- Giao diện thân thiện, responsive, dễ sử dụng

---

## 📦 Công nghệ sử dụng

| Backend               | Frontend           |
|----------------------|--------------------|
| Java 17              | Next.js 14 (App Router) |
| Spring Boot          | React 18           |
| Spring Security + JWT| TailwindCSS        |
| WebSocket            | TypeScript         |
| JPA + MySQL          | Formik, Axios      |

---

## 📁 Cấu trúc thư mục

├── backend/ # API & WebSocket (Spring Boot)
├── frontend/ # Giao diện người dùng (Next.js)


---

## 🚀 Cách chạy toàn bộ ứng dụng (local)

> 💡 Cần có Java 17, Node.js >= 18, MySQL

### 1️⃣ Clone project

```bash
git clone https://github.com/quannguyenhaminh18/QuizGymApp.git
cd QuizGymApp

2️⃣ Khởi động backend
cd backend

# Tạo file cấu hình
cp .env.example .env
# -> chỉnh thông tin DB, JWT, Google, Mail nếu cần

# Chạy ứng dụng
./gradlew bootRun
Mặc định chạy tại: http://localhost:8080

3️⃣ Khởi động frontend
cd ../frontend

# Cài dependencies
npm install

# Chạy dev server
npm run dev
Mặc định chạy tại: http://localhost:3000

🧪 Tài khoản mặc định
Email: admin@gmail.com
Password: Admin@123

🎯 Các tính năng chính
🧑‍🎓 Đăng nhập bằng tài khoản thường hoặc Google

📝 Làm bài trắc nghiệm với đồng hồ đếm ngược

💬 Thi online theo phòng – real-time bằng WebSocket

🧮 Tự động tính điểm và hiển thị xếp hạng

📊 Trang quản trị: tạo đề thi, xem lịch sử, thống kê

📄 Giấy phép
Dự án phát triển với mục đích học tập và trình diễn kỹ năng lập trình Java Fullstack.
Bạn có thể fork và sử dụng theo nhu cầu cá nhân.

