# minhauthen-template-1.21.8

## Giới thiệu

Đây là template dự án xác thực (authentication) sử dụng Java, được phát triển bởi MinhAnime. Dự án này cung cấp các thành phần cơ bản để xây dựng hệ thống xác thực người dùng cho ứng dụng Java.

## Tính năng

- Đăng ký/Đăng nhập người dùng
- Quản lý phiên đăng nhập (session)
- Hỗ trợ xác thực qua token
- Kiến trúc dễ dàng mở rộng

## Yêu cầu hệ thống

- Java 8 trở lên
- Maven hoặc Gradle để build project

## Cài đặt & Chạy thử

1. Clone repository:
   ```bash
   git clone https://github.com/MinhAnime/minhauthen-template-1.21.8.git
   ```
2. Build project:
   ```bash
   mvn clean install
   ```
3. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```
   (Hoặc sử dụng file JAR sau khi build)

## Cấu trúc thư mục

- `src/main/java`: Mã nguồn chính của dự án
- `src/main/resources`: File cấu hình và tài nguyên
- `README.md`: Tài liệu hướng dẫn

## Đóng góp

Hãy tạo pull request nếu bạn muốn đóng góp hoặc bổ sung tính năng mới!

## Tác giả

- MinhAnime

## License

MIT
