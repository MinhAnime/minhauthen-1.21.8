# Minhauthen – Minecraft Fabric Auth Mod

**Phiên bản:** 1.0.0  
**Hỗ trợ:** Minecraft 1.21.8 + Fabric Loader  

`Minhauthen` là mod quản lý **đăng ký, đăng nhập và token** cho người chơi trên server Fabric. Mod giúp bảo vệ server, đảm bảo chỉ người chơi có tài khoản hợp lệ mới thực hiện các hành động trong game.

---

## 1. Cài đặt

1. Cài Fabric Loader cho Minecraft 1.21.8.  
2. Copy file `Minhauthen.jar` vào thư mục `mods/` của server Fabric.  
3. Khởi động server để mod tạo thư mục cấu hình:


Trong đó sẽ có các file:  
- `users.json` → lưu tài khoản người chơi  
- `tokens.json` → lưu token do admin tạo  
- `last_locations.json` → lưu vị trí cuối cùng của người chơi  

---

## 2. Tính năng nổi bật

- **Quản lý đăng ký / đăng nhập:**  
  - Chặn di chuyển, phá block, đặt block, chat, sử dụng lệnh cho người chơi chưa đăng nhập.  
  - Đăng nhập thành công sẽ khôi phục vị trí trước đó và chuyển sang chế độ **SURVIVAL**.  

- **Token cho đăng ký:**  
  - Chỉ admin có quyền tạo token mới cho người chơi đăng ký.  
  - Mỗi token chỉ sử dụng được một lần.  
  - Nhập sai quá 5 lần → người chơi bị ban vĩnh viễn.  

- **Lưu vị trí người chơi:**  
  - Vị trí cuối cùng được lưu khi logout hoặc đăng xuất.  
  - Đăng nhập sẽ teleport người chơi về vị trí đã lưu hoặc spawn mặc định.  

- **Thông báo rõ ràng:**  
  - Biểu ngữ chào mừng lớn ở giữa màn hình khi đăng nhập.  
  - Thông báo lỗi rõ ràng khi đăng ký/đăng nhập thất bại.  

---

## 3. Quản lý Token

### Tạo token
Admin (permission level 2) tạo token để người chơi đăng ký:


- `<token>` là chuỗi do admin đặt (ví dụ `abc123`).  
- Token được lưu vào `tokens.json` và đánh dấu là **chưa dùng**.  

> Lưu ý: Một token chỉ dùng được một lần. Nếu nhập sai quá 5 lần, người chơi sẽ bị ban vĩnh viễn.

---

## 4. Đăng ký người chơi

Người chơi **chưa có tài khoản** cần đăng ký:


- `<password>`: mật khẩu muốn đặt  
- `<confirm>`: nhập lại mật khẩu  
- `<token>`: token hợp lệ do admin tạo  

**Kết quả:**
- Nếu đăng ký thành công:  
- Nếu token sai hoặc đã được sử dụng:  
- Nếu nhập mật khẩu không khớp:  

> Token được đánh dấu **đã dùng** ngay khi đăng ký thành công.

---

## 5. Đăng nhập

Người chơi **đã có tài khoản** dùng lệnh: `/login <password>`


- Đăng nhập thành công sẽ:
  - Chuyển người chơi về vị trí cuối cùng đã lưu (hoặc spawn mặc định nếu chưa có).  
  - Hiển thị biểu ngữ lớn chào mừng người chơi.  
  - Thay đổi chế độ chơi thành **SURVIVAL**.  

- Nếu chưa đăng nhập, các hành động sau bị chặn:  
  - Di chuyển  
  - Phá block, đặt block  
  - Tương tác với item hoặc entity  
  - Chat  
  - Sử dụng lệnh (ngoại trừ `/login` và `/register`)  

---

## 6. File lưu trữ

Mod tự quản lý các file JSON trong `config/minhauthen/`:

| File                 | Mục đích                                  |
|---------------------|------------------------------------------|
| `users.json`         | Lưu tên người chơi & mật khẩu             |
| `tokens.json`        | Lưu token do admin tạo, đánh dấu đã dùng |
| `last_locations.json`| Lưu vị trí cuối cùng của người chơi       |

> Mod tự động lưu khi có thay đổi hoặc khi server tắt.

---

## 7. Flow sử dụng

1. Admin tạo token:
2. Người chơi đăng ký:
3. Người chơi đăng nhập:

---

## 8. Ghi chú cho Admin

- Token nên được tạo ngẫu nhiên, tránh dễ đoán.  
- Theo dõi file `tokens.json` để kiểm soát token đã dùng.  
- Nếu cần reset token, chỉnh sửa trực tiếp trong `tokens.json`.  
- Người chơi nhập sai token hoặc mật khẩu quá 5 lần sẽ bị ban.  
- Nên sao lưu `users.json` và `last_locations.json` định kỳ.

---

## 9. Lưu ý

- Mod hiện chỉ hỗ trợ **Minecraft 1.21.8** với **Fabric**.  
- Không cần cấu hình thêm, mọi file JSON được quản lý tự động.  
- Biểu ngữ chào mừng và thông báo lỗi sử dụng text và mixin của mod.

---

## 10. Hỗ trợ

- Liên hệ tác giả qua Discord: `@m.minh_anime`  
- Báo lỗi GitHub: [repo-link](https://github.com/MinhAnime/minhauthen-1.21.8)
