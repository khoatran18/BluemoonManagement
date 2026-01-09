# API Contract – Hệ thống Quản lý Cư Dân & Chi Phí Chung Cư

Phiên bản: 2.1 \
Ngày cập nhật: 21/10/2025 \

**Tất cả dữ liệu: application/json**

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

Tất cả các method POST, PUT, DELETE chỉ có Admin hoặc FeeCollector mới có thể sử dụng

Response sẽ luôn gồm 4 trường:
```
{
  "success": false,
  "code": 404,
  "message": "Apartment not found",
  "data": null
}
```
Các ví dụ Resposne bên dưới s mc định nằm trong trường data.

Nếu API chứa query param theo mảng, truyền theo định dạng (ví dụ bên dưới)
```
/api/v1/fees?fee_type_id=1&fee_type_id=2
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
  * head_resident_id: string (optional)
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
        "apartment_id": 1,
        "building": "A",
        "room_number": "101",
        "head_resident": {
          "id": 3,
          "full_name": "Nguyen Van A",
          "phone": "0901234567"
        }
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
    "apartment_id": 1,
    "building": "A",
    "room_number": "101",
    "head_resident_id": 3,
    "residents": [
      {"resident_id": 3, "full_name": "Nguyen Van A"},
      {"resident_id": 10, "full_name": "Tran Thi B"}
    ]
  }
  ```

### 1.3. Tạo mới căn hộ

* Method: POST
* URL: /api/v1/apartments
* Role: Admin
* Request:

  ```json
  {
    "building": "A",
    "room_number": "101",
    "head_resident_id": 3,
    "residents": [
      {"id": 3},
      {"id": 10}
    ]
  }
  ```

### 1.4. Cập nhật căn hộ

* Method: PUT
* URL: /api/v1/apartments/{apartment_id}
* Role: Admin
* Request:

  ```json
  {
    "apartment_id": 1,
    "building": "A",
    "room_number": "101",
    "head_resident_id": 3,
    "residents": [
      {"id": 3},
      {"id": 10}
    ]
  }
  ```

### 1.5. Xóa căn hộ

* Method: DELETE
* URL: /api/v1/apartments/{apartment_id}
* Role: Admin

[//]: # (* Request:)

[//]: # ()
[//]: # (    ```json)

[//]: # (    {)

[//]: # (      "id": 1)

[//]: # (    })

[//]: # (    ```)

---

## 2. Cư dân (Resident)

### 2.1. Danh sách cư dân / Tìm kiếm nâng cao

* Method: GET
* URL: /api/v1/residents
* Role: Admin, Collector
* Query Parameters:

  * apartment_id: int (optional)
  * full_name: string (optional)
  * phone_number: string (optional)
  * email: string (optional)
  * page: number (optional, default=1)
  * limit: number (optional, default=10)
* Response:

  ```json
  {
    "page": 1,
    "limit": 10,
    "total_items": 25,
    "residents": [
      {
        "resident_id": 10,
        "full_name": "Tran Thi B",
        "email": "b@example.com",
        "phone_number": "0987654321",
        "apartment": {"id": 1, "building": "A", "room_number": "101"},
        "is_head": false
      }
    ]
  }
  ```

### 2.2. Xem chi tiết cư dân
* Method: GET
* URL: /api/v1/residents/{resident_id}
* Response
```json
    {
  "resident_id": 10,
  "full_name": "Tran Thi B",
  "email": "b@example.com",
  "phone_number": "0987654321",
  "apartment": {"id": 1, "building": "A", "room_number": "101"},
  "is_head": false
}
```

### 2.3. Thêm / Cập nhật / Xóa cư dân

* POST /api/v1/residents (Admin)
* Response:
```json
{
  "full_name": "Tran Thi A",
  "email": "",
  "phone_number": "",
  "apartment_id": 1,
}
```

* PUT /api/v1/residents/{id} (Admin)
* Response:
```json
{
  "resident_id": 1,
  "full_name": "Tran Thi A",
  "email": "",
  "phone_number": ""
}
```

* DELETE /api/v1/residents/{id} (Admin)
---

## 3. Loại phí & Danh mục phí (FeeType, FeeCategory)

### 3.1. Danh sách FeeType

* GET /api/v1/fee-types (Admin, Collector)
* Response:

  ```json
  {"fee-types": [
    {"id": 1, "name": "Định kỳ"},
    {"id": 2, "name": "Đột xuất"},
    {"id": 3, "name": "Tự nguyện"}
  ]}
  ```

### 3.2. Danh sách Fee Category

* GET /api/v1/fee-categories
* Query:
  * fee_type_id: number (optional)
  * page: number (optional, default=1)
  * limit: number (optional, default=10)
* Response:

  ```json
  {
  "page": 1,
  "limit": 10,
  "total_items": 25,
  "fee-categories": [
    {
      "fee_category_id": 1, 
      "name": "Điện", 
      "description": "Phí điện hàng tháng", 
      "fee_type_name": "Định kỳ"}
  ]}
  ```
* POST /api/v1/fee-categories (Admin)
* Request:
    ```json
    {
      "fee_type_id": 1,
      "name": "",
      "description": ""
    }
    ```
* PUT /api/v1/fee-categories/{fee_category_id} (Admin)
* Request:
    ```json
    {
      "fee_category_id": 2,
      "fee_type_id": 1,
      "name": "",
      "description": ""
    }
    ```
* DELETE /api/v1/fee-categories/{fee_category_id} (Admin)
---

## 4. Phí (Fee)

**Ví dụ về định dạng datetime:**
```
"applicable_month": "2025-06",
"effective_date": "2025-06-01",
"expiry_date": "2025-06-28",
"status": "ACTIVE" (Còn "DRAFT", "CLOSED", "ARCHIVED")
```

### 4.1. Tạo phí mới

* POST /api/v1/fees (Admin)
* Request body:

  ```json
  {
    "fee_type_id": 1,
    "fee_category_id": 1,
    "fee_name": "Phí điện tháng 10/2025",
    "fee_description": "",
    "fee_amount": 400000,
    "applicable_month": "2025-10",
    "effective_date": "",
    "expiry_date": "",
    "status": "ACTIVE"
  }
  ```

### 4.2. Danh sách phí

* GET /api/v1/fees
* Role: Admin
* Query:
  * fee_type_id: number (optional)
  * fee_category_id: number (optional)
  * fee_name: string (optional)
  * fee_amount: number (optional)
  * applicable_month: string (optional)
  * effective_date: string (optional)
  * expiry_date: string (optional)
  * status: string (optional)
  * page: number (optional, default=1)
  * limit: number (optional, default=10)
* Response:
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 25,
  "fees": [
    {
      "fee_id": 1,
      "fee_type_id": 1,
      "fee_category_id": 1,
      "fee_name": "",
      "fee_description": "",
      "fee_amount": 33.44,
      "applicable_month": "",
      "effective_date": "",
      "expiry_date": "",
      "status": ""
    }
  ]
}
```

### 4.3. Chi tiết phí

* GET /api/v1/fees/{fee_id}
* Response:
    ```json
    {
      "fee_id": 1,
      "fee_type_id": 1,
      "fee_category_id": 1,
      "fee_name": "",
      "fee_description": "",
      "fee_amount": 33.44,
      "applicable_month": "",
      "effective_date": "",
      "expiry_date": "",
      "status": ""
    }
    ```

### 4.4 Cập nhật phí
* PUT /api/v1/fees/{fee_id}
* Request:
  ```json
  {
    "fee_id": 1,
    "fee_type_id": 1,
    "fee_category_id": 1,
    "fee_name": "Phí điện tháng 10/2025",
    "fee_description": "",
    "fee_amount": 400000,
    "applicable_month": "2025-10",
    "effective_date": "",
    "expiry_date": "",
    "status": "ACTIVE"
  }
  ```

### 4.5. Xóa phí
* DELETE: /api/v1/fees/{fee_id}
* Request:
```json
{
  "fee_id": 1
}
```
---

## 5. Tình trạng phí căn hộ (ApartmentFeeStatus)

### 5.1. Xem tình trạng theo apartment_id (Phí chưa trả)

* GET /api/v1/apartment-fee-statuses/{apartment_id}
* Response:

```json
{
  "apartment_id": 1,
  "unpaid_fees": [
    { "fee_id": 5,
      "fee_name": "Phí điện tháng 10",
      "fee_amount": 400000,
      "fee_type_id":  1,
      "fee_type_name":  "Name",
      "fee_category_id":  2,
      "fee_category_name":  "Name",
      "effective_date":  "...", 
      "expiry_date":  "...",
      "fee_description":  "..."
    }
  ],
  "adjustments": [
    {
      "adjustment_id": 1, (Adjustment dành riêng cho một vài căn hộ sẽ có fee_id = -1)
      "fee_id": 2,
      "adjustment_amount": 100.2,
      "adjustment_type": "Type",
      "reason": "Do not know",
      "effective_date": "",
      "expiry_date": "",
    }
  ],
  "extra_adjustments": [
    {
      "adjustment_id": 1,
      "fee_id": -1, (Adjustment dành riêng cho một vài căn hộ sẽ có fee_id = -1)
      "adjustment_amount": 100.2,
      "adjustment_type": "Type",
      "reason": "Do not know",
      "effective_date": "",
      "expiry_date": "",
    }
  ],
  "total_paid": 20,
  "balance": 30,
  "updated_at": ""
}
```

### 5.2. Cập nhật tình trạng theo apartment_id

* PUT /api/v1/apartment-fee-statuses/{apartment_id}
* Request body:

```json
{
  "total_paid": 100,
  "balance": 20,
  (Fee bên dưới là thêm mới)
  "paid_fees": [
    {
      "fee_id":  4,
      "pay_amount": 30
    },
    {
      "fee_id": 12,
      "pay_amount": 50
    }
  ],
  // unpaid_fees cứ để [] trống thôi
  "unpaid_fees": [
    {"fee_id":  2}
  ]
}
```

---

## 6. Fee Adjustment

**Ví dụ về định dạng:**
```
"adjustment_type": "decrease", (Còn "decrease")
"reason": "Ưu đãi/Phụ phí đợt 10",
"effective_date": "2025-06-01",
"expiry_date": "2025-12-31"
```

### 6.1. Tạo Adjustment

* POST /api/v1/adjustments (Admin)
* Request body:

  ```json
  {
    "fee_id": 5,
    "adjustment_amount": 50000,
    "adjustment_type": "decrease",
    "reason": "Giảm phí do sự cố",
    "effective_date": "2025-10-15",
    "expiry_date": "2025-10-15"
  }
  ```

### 6.2. Chỉnh sửa Adjustment

* PUT /api/v1/adjustments/adjustment_id (Admin)
* Request body:
  ```json
  {
    "adjustment_id": 1,
    "fee_id": 5,
    "adjustment_amount": 50000,
    "adjustment_type": "decrease",
    "reason": "Giảm phí do sự cố",
    "effective_date": "2025-10-15",
    "expiry_date": "2025-10-15"
  }
  ```

### 6.3. Lấy danh sách Adjustment
* GET /api/v1/adjustments (Admin)
* Query:
  * page: optional (default=10)
  * limit: optional (default=1)
  * fee_id: number (optional)
  * adjustment_amount: number (optional)
  * adjustment_type: number (optional)
  * effective_date: string (optional)
  * expiry_date: string (optional)

* Response:
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 25,
  "adjustments": [
    {
      "adjustment_id": 1,
      "fee_id": 5,
      "adjustment_amount": 50000,
      "adjustment_type": "decrease",
      "reason": "Giảm phí do sự cố",
      "effective_date": "2025-10-15",
      "expiry_date": "2025-10-15"
    }
  ]
}
```

### 6.4. Lấy Adjustment cụ thể
* GET /api/v1/adjustments/adjustment_id (Admin)
* Response:
    ```json
        {
          "adjustment_id": 1,
          "fee_id": 5,
          "adjustment_amount": 50000,
          "adjustment_type": "decrease",
          "reason": "Giảm phí do sự cố",
          "effective_date": "2025-10-15",
          "expiry_date": "2025-10-15"
        }
    ```

### 6.5. Xóa Adjustment
* DELETE /api/v1/adjustments/adjustment_id (Admin)

---

## 7. Gán Adjustment đặc quyền cho từng Apartment

### 7.1. Lấy danh sách ApartmentSpecificAdjustment

* GET /api/v1/adjustments/apartment_specific_adjustments
* Query:
  * page: optional (default=10)
  * limit: optional (default=1)
  * adjustment_amount: number (optional)
  * adjustment_type: number (optional)
  * effective_date: string (optional)
  * expiry_date: string (optional)

* Response:
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 25,
  "adjustments": [
    {
      "adjustment_id": 1,
      "fee_id": -1,
      "adjustment_amount": 50000,
      "adjustment_type": "decrease",
      "reason": "Giảm phí do sự cố",
      "effective_date": "2025-10-15",
      "expiry_date": "2025-10-15"
    }
  ]
}
```

### 7.2. Lấy danh sách ApartmentSpecificAdjustment theo ApartmentID

* GET /api/v1/apartments/apartment_specific_adjustments/{apartment_id}
* Response:
```json
{
  "apartment_specific_adjustments": [
    {
      "adjustment_id": 1,
      "adjustment_amount": 50000,
      "fee_id": -1,
      "adjustment_type": "decrease",
      "reason": "Giảm phí do sự cố",
      "effective_date": "2025-10-15",
      "expiry_date": "2025-10-15"
    }
  ]
}
```

### 7.3. Cập nhật thông tin ApartmentSpecificAdjustment theo apartment_id

* PUT /api/v1/apartments/apartment_specific_adjustments/{apartment_id}
* Request:

```json
{
  "adjustment_ids": [1, 3, 5, 8, 9]
}
```


---
## 8. Lịch sử giao dịch

### 8.1. Lấy danh sách PayHistory
GET /api/v1/pay-histories

* Query:
  * page: optional (default=10)
  * limit: optional (default=1)
  * apartment_id: number (optional)
  * fee_id: number (optional)

* Response:
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 2,
  "pay_histories": [
      {
          "pay_history_id": 5,
          "apartment_id": 51,
          "fee_id": 4,
          "fee_name": "Phí Vệ Sinh tháng 2025-07",
          "fee_type_name": "OBLIGATORY",
          "fee_category_name": "Phí Vệ Sinh",
          "pay_datetime": "2026-01-01",
          "pay_amount": 30.00,
          "pay_note": "Hoàn thành phí"
      },
      {
          "pay_history_id": 6,
          "apartment_id": 51,
          "fee_id": 12,
          "fee_name": "Phí Gửi Xe Máy tháng 2025-06",
          "fee_type_name": "OBLIGATORY",
          "fee_category_name": "Phí Gửi Xe Máy",
          "pay_datetime": "2026-01-01",
          "pay_amount": 50.00,
          "pay_note": "Hoàn thành phí"
      }
  ]
}
```

### 8.2. GET PayHistory theo PayHistoryID
GET /api/v1/adjustments/pay-histories/{pay_history_id}

* Response:
```json
{
  "pay_history_id": 6,
  "apartment_id": 51,
  "fee_id": 12,
  "fee_name": "Phí Gửi Xe Máy tháng 2025-06",
  "fee_type_name": "OBLIGATORY",
  "fee_category_name": "Phí Gửi Xe Máy",
  "pay_datetime": "2026-01-01",
  "pay_amount": 50.00,
  "pay_note": "Hoàn thành phí"
}
```
---

## 9. Lịch sử thay đổi balance (Do adjustment)

### 9.1. Lấy danh sách AdjustmentBalance
GET /api/v1/adjustment-balances

* Query:
  * page: optional (default=10)
  * limit: optional (default=1)
  * apartment_id: number (optional)
  * fee_id: number (optional)
  * adjustment_id: number (optional)

* Response:
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 16,
  "adjustment_balances": [
    {
      "adjustment_balance_id": 1,
      "apartment_id": 9,
      "fee_id": 6,
      "adjustment_id": 33,
      "fee_name": "Tiền Nước tháng 2025-06",
      "adjustment_reason": "Tăng phí do ba sàn quá đà",
      "old_balance": 150000.00,
      "new_balance": 100000.00,
      "fee_start_datetime": "2025-06-01",
      "adjustment_start_datetime": "2025-10-15",
      "adjustment_balance_note": "Thay đổi phí"
    },
    {
      "adjustment_balance_id": 2,
      "apartment_id": 3,
      "fee_id": 6,
      "adjustment_id": 33,
      "fee_name": "Tiền Nước tháng 2025-06",
      "adjustment_reason": "Tăng phí do ba sàn quá đà",
      "old_balance": 150000.00,
      "new_balance": 100000.00,
      "fee_start_datetime": "2025-06-01",
      "adjustment_start_datetime": "2025-10-15",
      "adjustment_balance_note": "Thay đổi phí"
    }
  ]
}
```
---

### 9.2. Lấy AdjustmentBalance theo AdjustmentBalanceID
GET /api/v1/adjustments/adjustment-balances/{adjustment_balance_id}

* Response:
```json
{
  "adjustment_balance_id": 1,
  "apartment_id": 9,
  "fee_id": 6,
  "adjustment_id": 33,
  "fee_name": "Tiền Nước tháng 2025-06",
  "adjustment_reason": "Tăng phí do ba sàn quá đà",
  "old_balance": 150000.00,
  "new_balance": 100000.00,
  "fee_start_datetime": "2025-06-01",
  "adjustment_start_datetime": "2025-10-15",
  "adjustment_balance_note": "Thay đổi phí"
}
```

---

## 10. Report API

### 10.1. Lấy report apartment
GET /api/v1/reports/apartment_common

* Response:
```json
{
  "resident_total": 140,
  "room_total": 51,
  "building_total": 2
}
```
---

### 10.2. Lấy report fee
GET /api/v1/reports/fee_common

* Response:
```json
{
  "total_paid_fee_amount": 160.00,
  "active_fee_count": 23,
  "draft_fee_count": 0,
  "closed_fee_count": 6,
  "archived_fee_count": 0
}
```

---

## 11. Account

**Các Role:** Admin, Citizen, FeeCollector

### 11.1. Đăng ký

* POST /api/v1/auth_service/register
* Request body:

  ```json
  {
  "username": "new_resident",
  "password": "password123",
  "email": "resident@example.com", (email này là email của user tương ứng)
  "identity_number": "987654321", (12 chữ số)
  "role": "Citizen"
  }
  ```

### 11.2. Đăng nhập
(Mọi lỗi không thành công đều quy về sai username/password)
* PUT /api/v1/auth_service/login
* Request body:
  ```json
  {
   "username": "admin_user",
   "password": "hashed_password"
  }
  ```

* Response:
```json
{
  "username": "admin_user",
  "email": "admin@example.com",
  "identity_number": "123456789", (12 chữ số)
  "role": "Admin",
  "access_token": "eyJhbG...",
  "refresh_token": "eyJhbG..."
}
```

### 11.3. Thay đổi mật khẩu
* GET /api/v1/auth_service/change-password

* Request
```json
{
  "username": "admin_user",
  "old_password": "old",
  "new_password": "new"
}
```

### 11.4. Refresh Token
* GET /api/v1/adjustments/refresh
* Request:

```json
{
  "access_token": "old_expired_access_token",
  "refresh_token": "current_valid_refresh_token"
}
```
* Response:
    ```json
        {
          "access_token": "new_access_token",
          "refresh_token": "new_refresh_token"
        }   
    ```

---

## 12. Lịch sử xóa căn hộ (Delete Apartment History)

### 12.1. Lấy danh sách lịch sử xóa Apartment

**GET** `/api/v1/delete-apartment-histories`

### Query Parameters
- `page` (optional, default = 1)
- `limit` (optional, default = 10)
- `apartment_id` (optional)

### Response
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 2,
  "delete_apartment_histories": [
    {
      "history_id": 1,
      "apartment_id": 51,
      "building": "A",
      "room_number": "1203",
      "deleted_at": "2026-01-02T10:15:30",
    }
  ]
}
```

### 12.2. Lấy lịch sử xóa Apartment theo HistoryID

**GET** `/api/v1/delete-apartment-histories/{history_id}`

### Response
```json
{
  "history_id": 1,
  "apartment_id": 51,
  "building": "A",
  "room_number": "1203",
  "deleted_at": "2026-01-02T10:15:30",
}

```

---

## 13. Lịch sử xóa cư dân (Delete Resident History)

### 13.1. Lấy danh sách lịch sử xóa Resident

**GET** `/api/v1/delete-resident-histories`

### Query Parameters
- `page` (optional, default = 1)
- `limit` (optional, default = 10)
- `resident_id` (optional)
- `apartment_id` (optional)

### Response
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 1,
  "delete_resident_histories": [
    {
      "history_id": 5,
      "resident_id": 21,
      "apartment_id": 51,
      "full_name": "Nguyễn Văn A",
      "phone_number": "0987654321",
      "email": "vana@example.com",
      "is_head": false,
      "deleted_at": "2026-01-04T14:45:00",
    }
  ]
}

```

### 13.2. Lấy lịch sử xóa Resident theo HistoryID

**GET** `/api/v1/delete-resident-histories/{history_id}`

### Response
```json
{
  "history_id": 5,
  "resident_id": 21,
  "apartment_id": 51,
  "full_name": "Nguyễn Văn A",
  "phone_number": "0987654321",
  "email": "vana@example.com",
  "is_head": false,
  "deleted_at": "2026-01-04T14:45:00",
}
```

---

## 14. Lịch sử xóa khoản phí (Delete Fee History)

### 14.1. Lấy danh sách lịch sử xóa Fee

**GET** `/api/v1/delete-fee-histories`

### Query Parameters
- `page` (optional, default = 1)
- `limit` (optional, default = 10)
- `fee_id` (optional)
- `fee_type_id` (optional)

### Response
```json
{
  "page": 1,
  "limit": 10,
  "total_items": 1,
  "delete_fee_histories": [
    {
      "history_id": 3,
      "fee_id": 12,
      "fee_type_id": 2,
      "fee_category_id": 4,
      "fee_name": "Phí Gửi Xe Máy tháng 2025-06",
      "fee_description": "Thu phí gửi xe máy",
      "applicable_month": "2025-06",
      "amount": 50000.00,
      "start_date": "2025-06-01",
      "end_date": "2025-06-30",
      "status": "ARCHIVED",
      "deleted_at": "2026-01-05T11:00:00",
    }
  ]
}
```

### 14.2. Lấy lịch sử xóa Fee theo HistoryID

**GET** `/api/v1/delete-fee-histories/{history_id}`

### Response
```json
{
  "history_id": 3,
  "fee_id": 12,
  "fee_type_id": 2,
  "fee_category_id": 4,
  "fee_name": "Phí Gửi Xe Máy tháng 2025-06",
  "fee_description": "Thu phí gửi xe máy",
  "applicable_month": "2025-06",
  "amount": 50000.00,
  "start_date": "2025-06-01",
  "end_date": "2025-06-30",
  "status": "ARCHIVED",
  "deleted_at": "2026-01-05T11:00:00",
}
```


---

## 15. Enum & Trạng thái

### FeeStatus

* DRAFT, ACTIVE, CLOSED, ARCHIVED

### PaymentStatus

* Paid, Unpaid, Overdue, PartiallyPaid

### AdjustmentType

* increase, decrease

### ReminderType

* created, mid_term, due_soon, overdue

### RoleType

* Admin, FeeCollector, Citizen
