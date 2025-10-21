# API Contract – Hệ thống Quản lý Cư Dân & Chi Phí Chung Cư

Phiên bản: 2.1 \
Ngày cập nhật: 21/10/2025

## Tổng quan

Hệ thống quản lý cư dân và chi phí chung cư bao gồm các chức năng chính:

* Quản lý thông tin cư dân và căn hộ.
* Quản lý các loại phí (định kỳ, đột xuất, tự nguyện).
* Theo dõi tình trạng thu phí, nợ phí, lịch sử thanh toán.
* Ghi nhận, điều chỉnh, nhắc nhở và báo cáo thu phí.
* Nhập liệu và chỉnh sửa dữ liệu khi có sai sót.
* Quản lý phân quyền người dùng (Admin, Collector, Resident).

Tất cả các API đều yêu cầu xác thực bằng Bearer Token:

```
Headers: Authorization: Bearer {token}
```

---

## 1. Căn hộ (Apartment)

### 1.1. Danh sách căn hộ

* Method: GET
* URL: /api/v1/apartments
* Role: Admin, Collector
* Query Parameters:

    * building: string (optional)
    * room_number: string (optional)
    * page: number (optional, default=1)
    * limit: number (optional, default=10)
* Response:

  ```json
  {
    "page": 1,
    "limit": 10,
    "total_items": 50,
    "items": [
      {
        "id": 1,
        "building": "A",
        "room_number": "101",
        "head_resident": {
          "id": 3,
          "full_name": "Nguyen Van A",
          "phone": "0901234567"
        },
        "created_at": "2025-09-01T00:00:00Z",
        "updated_at": "2025-09-20T00:00:00Z"
      }
    ]
  }
  ```

### 1.2. Chi tiết căn hộ

* Method: GET
* URL: /api/v1/apartments/{apartment_id}
* Role: Admin, Collector, Resident (nếu là căn hộ của họ)
* Response:

  ```json
  {
    "id": 1,
    "building": "A",
    "room_number": "101",
    "head_resident_id": 3,
    "residents": [
      {"id": 3, "full_name": "Nguyen Van A"},
      {"id": 10, "full_name": "Tran Thi B"}
    ],
    "fees_summary": {
      "total_due": 1200000,
      "total_paid": 900000,
      "balance": 300000
    }
  }
  ```

---

## 2. Cư dân (Resident)

### 2.1. Danh sách cư dân / Tìm kiếm nâng cao

* Method: GET
* URL: /api/v1/residents
* Role: Admin, Collector
* Query Parameters:

    * apartment_id: int (optional)
    * name: string (optional)
    * status: string (optional: Paid, Unpaid, Overdue)
* Response:

  ```json
  {
    "items": [
      {
        "id": 10,
        "full_name": "Tran Thi B",
        "email": "b@example.com",
        "phone": "0987654321",
        "apartment": {"id": 1, "building": "A", "room_number": "101"},
        "is_head": false,
        "debt_months": 2,
        "total_due": 600000,
        "last_payment_date": "2025-09-30"
      }
    ]
  }
  ```

### 2.2. Thêm / Cập nhật / Xóa cư dân

* POST /api/v1/residents (Admin)
* PUT /api/v1/residents/{id} (Admin)
* DELETE /api/v1/residents/{id} (Admin)

### 2.3. Xem các khoản phí hiện có & thông báo liên quan

* Method: GET
* URL: /api/v1/residents/{resident_id}/fees
* Role: Resident, Collector, Admin
* Mục đích: Cư dân có thể xem toàn bộ các khoản phí hiện tại cần đóng, kèm các thông báo và nhắc nhở liên quan.
* Response:

  ```json
  {
    "resident_id": 3,
    "full_name": "Nguyen Van A",
    "apartment": {"id": 1, "building": "A", "room_number": "101"},
    "fees": [
      {
        "fee_id": 5,
        "name": "Phí điện tháng 10",
        "amount": 400000,
        "status": "Unpaid",
        "due_date": "2025-10-30",
        "notifications": [
          {"type": "due_soon", "sent_at": "2025-10-18T10:00:00", "status": "Delivered"},
          {"type": "overdue", "sent_at": "2025-10-21T08:00:00", "status": "Seen"}
        ]
      }
    ],
    "total_due": 400000,
    "total_paid": 0,
    "balance": 400000
  }
  ```

---

## 3. Loại phí & Danh mục phí (FeeType, FeeCategory)

### 3.1. Danh sách loại phí

* GET /api/v1/fee-types (Admin, Collector)
* Response:

  ```json
  {"items": [
    {"id": 1, "name": "Định kỳ"},
    {"id": 2, "name": "Đột xuất"},
    {"id": 3, "name": "Tự nguyện"}
  ]}
  ```

### 3.2. Quản lý danh mục phí

* GET /api/v1/fee-categories
* POST /api/v1/fee-categories (Admin)
* Response:

  ```json
  {"items": [
    {"id": 1, "name": "Điện", "description": "Phí điện hàng tháng", "fee_type": "Định kỳ"}
  ]}
  ```

---

## 4. Phí (Fee)

### 4.1. Tạo phí mới

* POST /api/v1/fees (Admin)
* Request body:

  ```json
  {
    "fee_type_id": 1,
    "fee_category_id": 1,
    "fee_name": "Phí điện tháng 10/2025",
    "fee_amount": 400000,
    "applicable_period": "2025-10",
    "frequency": "monthly",
    "status": "Active"
  }
  ```

### 4.2. Danh sách phí

* GET /api/v1/fees?status=Active&month=2025-10

### 4.3. Chi tiết phí

* GET /api/v1/fees/{fee_id}

---

## 5. Tình trạng phí căn hộ (ApartmentFeeStatus)

### 5.1. Xem tổng hợp phí căn hộ

* GET /api/v1/apartments/{id}/fees
* Response:

  ```json
  {
    "apartment_id": 1,
    "fees": [
      {"fee_id": 5, "name": "Phí điện tháng 10", "amount": 400000, "status": "Unpaid"},
      {"fee_id": 6, "name": "Phí gửi xe", "amount": 100000, "status": "Paid"}
    ],
    "total_fee": 500000,
    "total_paid": 100000,
    "balance": 400000
  }
  ```

### 5.2. Cập nhật trạng thái thanh toán

* PUT /api/v1/apartments/{id}/fees/{fee_id}/status
* Request body:

  ```json
  {"status": "Paid"}
  ```

---

## 6. Điều chỉnh & Nhập liệu (Adjustment, Import)

### 6.1. Tạo điều chỉnh phí

* POST /api/v1/adjustments (Admin)
* Request body:

  ```json
  {
    "fee_id": 5,
    "adjustment_amount": -50000,
    "adjustment_type": "decrease",
    "reason": "Giảm phí do sự cố",
    "effective_date": "2025-10-15"
  }
  ```

### 6.2. Import chỉnh sửa dữ liệu

* POST /api/v1/import/fees
* Content-Type: multipart/form-data
* Role: Admin

---

## 7. Báo cáo & Phân quyền

### 7.1. Báo cáo tổng hợp thu phí

* GET /api/v1/reports/summary?month=2025-10

### 7.2. Xuất báo cáo cư dân cụ thể

* GET /api/v1/reports/residents/{resident_id}

### 7.3. Phân quyền người dùng

* GET /api/v1/users
* PUT /api/v1/users/{id}/role

---

## 8. Enum & Trạng thái

### FeeStatus

* Draft, Active, Closed, Archived

### PaymentStatus

* Paid, Unpaid, Overdue, PartiallyPaid

### AdjustmentType

* increase, decrease

### ReminderType

* created, mid_term, due_soon, overdue

### RoleType

* Admin, Collector, Resident
