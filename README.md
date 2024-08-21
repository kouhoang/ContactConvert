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

## Hướng Dẫn Cài Đặt

1. Clone repository này về máy của bạn:
   ```bash
   git clone https://github.com/kouhoang/ContactConvert.git
