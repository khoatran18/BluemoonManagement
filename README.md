# Dự án quản lý thu phí Chung cư Bluemoon

## 1. Giới thiệu

Hệ thống **Quản lý thu phí Chung cư Bluemoon** là phần mềm hỗ trợ ban quản lý trong việc tổ chức, theo dõi và kiểm soát toàn bộ hoạt động thu – chi, nhân khẩu và vận hành chung cư một cách tập trung, chính xác và minh bạch.

Hệ thống mô phỏng đầy đủ các nghiệp vụ thực tế trong quản lý chung cư như: quản lý tòa nhà, căn hộ, hộ khẩu, nhân khẩu, các đợt thu phí, phát sinh tăng giảm phí, lịch sử giao dịch và thống kê báo cáo.


---

## 2. Mục tiêu

### Mục tiêu tổng quát
Xây dựng hệ thống quản lý thu phí chung cư có kiến trúc rõ ràng, nghiệp vụ đầy đủ, dữ liệu nhất quán, phục vụ hiệu quả cho ban quản lý và cư dân.

### Mục tiêu cụ thể

- Quản lý thông tin **tòa nhà – phòng – hộ gia đình**
- Quản lý **các loại phí** và các đợt thu phí theo thời gian
- Theo dõi chi tiết tình trạng thu phí của từng căn hộ
- Ghi nhận các thay đổi tăng / giảm phí phát sinh
- Lưu trữ và truy vết lịch sử giao dịch
- Thống kê số liệu dân cư và chi phí
- Phân quyền rõ ràng theo vai trò sử dụng hệ thống
- Thiết kế API rõ ràng, dễ mở rộng và tích hợp frontend

---

## 3. Tính năng

### 3.1 Quản lý tòa nhà
- Tạo, cập nhật, xóa thông tin tòa nhà
- Quản lý danh sách các phòng thuộc từng tòa
- Thống kê số lượng phòng, số hộ, số nhân khẩu theo tòa

### 3.2 Quản lý phòng (căn hộ)
- Quản lý thông tin phòng: mã phòng, tầng, diện tích, trạng thái
- Gắn phòng với tòa nhà
- Theo dõi tình trạng sử dụng (đang ở / trống)
- Liên kết phòng với hộ khẩu

### 3.3 Quản lý hộ khẩu
- Quản lý thông tin hộ gia đình
- Gắn hộ khẩu với phòng
- Xác định chủ hộ
- Theo dõi trạng thái hộ (đang cư trú / đã chuyển đi)

### 3.4 Quản lý nhân khẩu
- Quản lý danh sách nhân khẩu trong từng hộ
- Phục vụ thống kê dân cư

### 3.5 Quản lý thu phí
- Quản lý danh mục các loại phí:
    - Phí quản lý
    - Phí điện
    - Phí nước
    - Phí gửi xe
    - Phí dịch vụ khác
    - ...
- Thiết lập đơn giá, chu kỳ thu
- Kích hoạt / ngừng áp dụng từng loại phí

### 3.6 Quản lý đợt thu phí
- Tạo các đợt thu phí theo tháng hoặc theo kỳ
- Tự động sinh danh sách khoản thu cho từng phòng
- Gán trạng thái cho từng khoản:
    - Chưa thu
    - Đã thu
    - Quá hạn
- Cho phép cập nhật trạng thái khi đã thanh toán

### 3.7 Quản lý tăng – giảm thu phí
- Ghi nhận các thay đổi phát sinh:
    - Tăng phí (phụ thu, dịch vụ bổ sung)
    - Giảm phí (miễn giảm, điều chỉnh sai sót)
- Lưu lý do điều chỉnh
- Áp dụng cho từng phòng hoặc từng đợt thu
- Lưu lịch sử thay đổi để đối soát


### 3.8 Lịch sử giao dịch
- Lưu toàn bộ lịch sử thanh toán
- Ghi nhận:
    - Thời gian giao dịch
    - Số tiền
    - Loại phí
    - Người thực hiện
- Phục vụ tra cứu, đối chiếu và kiểm toán

### 3.9 Thống kê nhân khẩu
- Thống kê tổng số nhân khẩu
- Thống kê theo tòa / theo phòng

### 3.10 Thống kê chi phí
- Thống kê tổng thu theo:
    - Thời gian
    - Loại phí
    - Tòa / phòng
- Thống kê công nợ chưa thu

---


## 4. Kiên trúc hệ thống

### Tổ chức thư mục:
```
project/
├── src/                                # Root code
│   ├── frontend/                       # React app
│   │   ├── app/                
│   │   ├── features/          
│   │   │   ├── module/
│   │   │   └── ...
│   │   ├── util/       
│   │   └── test/                       # Quarkus app
│   └── backend/
│       ├── common-package/             # Package for common logic: Exception, MockData
│       │   ├── src/
│       │   ├── pom.xml
│       │   └── ...
│       ├── resident_fee-service/       # Core backend service
│       │   ├── src/
│       │   ├── pom.xml
│       │   └── ...
│       ├── Dockerfile   
│       ├── pom.xml      
│       ├── README.md                   # Backend setup and start
│       ├── README-dev.md               # Backend Requirement
│       ├── README-quarkus.md           # Quarkus md
│       └── ...  
├── tools/                              # Tools
│   └── docker/                         # Docker-compose file
├── docs/     
│   ├── convention/                     # Git convention
│   └── sprint1/                        # Document for Sprint 1
└── README.md
```

### Kiến trúc Frontend

### Kiến trúc Backend
Xem chi tiết tại [Kiến trúc hệ thống Backend](./src/backend/README.md)



## 5. Cài đặt và Sử dụng

**Lưu ý: Toàn bộ hệ thống đã được triển khai lên Cloud, hướng dẫn bên dưới chỉ phục vụ việc chạy local**

### Backend

```bash
cd tools/docker
docker compose -f docker_backend.yml up -d
```
Truy cập ```localhost:8080```

Khi muốn xóa:

```bash
docker compose -f docker_backend.yml down -v
```

---

## 6. Thành viên dự án

| Tên            | Vai trò              | 
|----------------|----------------------|
| Trần Minh Khoa | Team Leader, Backend |
|                |                      | 
|                |                      | 
|                |                      | 
|                |                      |  

---
