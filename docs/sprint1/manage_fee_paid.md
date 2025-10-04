``` mermaid
flowchart TD
    A(["Ban quản lý"]) --> A1["Chọn 'Thanh toán' trong Dashboard"]
    A1 --> B["Chọn 'Ghi nhận thanh toán'"]
    B --> C["Nhập mã cư dân / mã căn hộ"]
    C --> X{"Cư dân tồn tại?"}
    X -- Không --> L["Hiển thị lỗi: Không tìm thấy cư dân"]
    L --> B
    X -- Có --> D["Hệ thống kiểm tra các khoản phí chưa thanh toán"]
    D --> Y{"Có phí chưa thanh toán?"}
    Y -- Không --> M["Thông báo: Không có khoản nợ"]
    M --> I["Ban quản lý chọn 'Xem trạng thái thanh toán'"]
    Y -- Có --> E["Ban quản lý chọn khoản phí + nhập số tiền, ngày, phương thức thanh toán"]
    E --> Z{"Dữ liệu nhập hợp lệ?"}
    Z -- Không --> N["Hiển thị lỗi: Nhập dữ liệu không hợp lệ"]
    N --> E
    Z -- Có --> F["Nhấn 'Xác nhận'"]
    F --> G["Hệ thống lưu dữ liệu thanh toán vào DB"]
    G --> W{"Lỗi DB?"}
    W -- Có --> O["Hiển thị lỗi: 'Lưu DB thất bại'. Hủy giao dịch"]
    O --> F
    W -- Không --> H["Hệ thống cập nhật trạng thái phí"]
    H --> Q{"Kiểm tra số tiền thanh toán"}
    Q -- Đủ số tiền --> R["Trạng thái = Đã đóng"]
    Q -- 0 đồng --> S["Trạng thái = Chưa đóng"]
    Q -- Một phần --> T["Trạng thái = Còn nợ"]
    Q -- Thừa tiền --> U["Trạng thái = Đã đóng + Ghi nhận dư"]
    R --> I
    S --> I
    T --> I
    U --> I
    I --> J["Hệ thống hiển thị danh sách cư dân + trạng thái: Đã đóng / Chưa đóng / Còn nợ / Có dư"]
    J --> K(["Kết thúc"])
    G --> n1["(Nút chưa đặt tên)"]

```