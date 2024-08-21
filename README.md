# Ứng Dụng ContactConvert

Đây là một ứng dụng Android cho phép người dùng quản lý danh bạ của họ. Ứng dụng cung cấp các chức năng như thêm, xóa, và chuyển đổi số điện thoại dựa trên các mẫu nhất định.

## Tính Năng

- **Thêm danh bạ**: Người dùng có thể thêm một liên hệ mới với tên và số điện thoại.
- **Xóa danh bạ đã chọn**: Cho phép xóa các liên hệ được chọn trong danh sách.
- **Chuyển đổi số điện thoại**: Tự động chuyển đổi các số điện thoại bắt đầu bằng "0167" hoặc "84167" thành "037".

## Cấu Trúc Dự Án

- **MainActivity**: Là màn hình chính, hiển thị danh sách các liên hệ và các nút thao tác như thêm, xóa, và chuyển đổi số điện thoại.
- **AddContactActivity**: Là màn hình để người dùng nhập thông tin liên hệ mới.
- **ContactAdapter**: Adapter cho RecyclerView để hiển thị danh sách liên hệ.
- **Contact**: Data class đại diện cho một liên hệ.

## Quyền (Permissions)

Ứng dụng yêu cầu các quyền sau:

- `READ_CONTACTS`: Đọc danh bạ từ thiết bị.
- `WRITE_CONTACTS`: Ghi và chỉnh sửa danh bạ trên thiết bị.

## Cài đặt

1. **Clone repo:**

    ```sh
    git clone https://github.com/kouhoang/ContactConvert.git
    ```

2. **Mở project trong Android Studio:**

    - Chọn **File** -> **Open**.
    - Dẫn đến thư mục vừa clone và chọn **Open**.

3. **Build project:**
    - Đảm bảo rằng các dependencies cần thiết được cài đặt.
    - Build project bằng cách chọn **Build** -> **Make Project**.

4. **Run project:**
    - Chọn một thiết bị ảo hoặc kết nối thiết bị thật để chạy ứng dụng.
    - Nhấn nút **Run** để cài đặt và chạy ứng dụng trên thiết bị.

