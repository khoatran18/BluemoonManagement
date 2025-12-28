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
    "room_number": "101"
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
* PUT /api/v1/fee-categories (Admin)
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
    {"fee_id":  1},
    {"fee_id":  3}
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

* POST /api/v1/adjustments/adjustment_id (Admin)
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

## 8. Enum & Trạng thái

### FeeStatus

* DRAFT, ACTIVE, CLOSED, ARCHIVED

### PaymentStatus

* Paid, Unpaid, Overdue, PartiallyPaid

### AdjustmentType

* increase, decrease

### ReminderType

* created, mid_term, due_soon, overdue

### RoleType

* Admin, Collector, Resident
