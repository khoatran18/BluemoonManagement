# Công nghệ sử dụng và kiến trúc hệ thống Backend

## 1. Giới thiệu

Dự án áp dụng mô hình 3 lớp (Resource – Service – Repository) và sử dụng **PanacheRepository** để truy cập cơ sở dữ
liệu.  
Các tầng được tách biệt thông qua **DTO** và **Mapper**. \
Ví dụ cụ thể xem trong module `test-service`.

---

## 2. Công nghệ sử dụng

| Thành phần    | Công nghệ                         |
|---------------|-----------------------------------|
| Ngôn ngữ      | Java 17                           |
| Framework     | Quarkus 3.x                       |
| ORM           | Hibernate ORM (PanacheRepository) |
| Cơ sở dữ liệu | PostgreSQL                        |
| Build tool    | Maven                             |
| API           | REST (Jakarta REST)               |

---

## 3. Cấu trúc thư mục dự án

```markdown
src/main/java/com/project/
│
├── resource/ # Lớp REST API (nhận request, trả response)
│
├── service/ # Lớp xử lý nghiệp vụ, quản lý transaction
│
├── repository/ # Lớp truy cập dữ liệu (PanacheRepository)
│
├── entity/ # Các thực thể ORM ánh xạ sang bảng SQL
│
├── dto/ # Đối tượng truyền dữ liệu giữa resource và service
│
├── mapper/ # Chuyển đổi Entity ↔ DTO
│
├── exception/ # Xử lý ngoại lệ tập trung
│
├── config/ # Cấu hình ứng dụng
│
└── util/ # Các tiện ích dùng chung
```

- Khi Client gửi JSON Request đến Resource, JSON tự parse sang DTO (Sử dụng Jackson - xem code trong package dto).
- Resource chuyển DTO xuống Service.
- Service nhận, xử lý, gọi các hàm trong Repository.
- Nếu có lỗi, service throw error, nếu pass hết thì return DTO cho resource.
- Resource gom lại, trả ApiResponse cho client.

--> Resource nhận JSON Request và trả ApiResponse \
--> Service giao tiếp qua lại với Resource thông qua DTO \
--> Mapper biến đổi Entity và DTO qua lại với nhau


## 4. Dòng dữ liệu

```markdown
Client (JSON Request)
        ↓ (json)
Resource (parse JSON → DTO)
        ↓ (DTO)
Service (xử lý nghiệp vụ, gọi repository, mapper, throw exception nếu cần)
        ↓ 
Repository (làm việc trực tiếp với database qua Entity)
        ↓
Entity (ánh xạ bảng dữ liệu)
        ↓
Service (chuyển Entity → DTO)
        ↓ (DTO)
Resource (đóng gói response với ApiResponse)
        ↓
GlobalExceptionMapper (nếu có lỗi)
        ↓
Client (JSON Response)
```

## 5. Quy ước đặt tên

### Class

| Thành phần         | Quy ước                                | Ví dụ                                     |
|--------------------|----------------------------------------|-------------------------------------------|
| **Entity**         | PascalCase                             | `Fee`, `Apartment`                        |
| **DTO**            | PascalCase + hậu tố `DTO`              | `ResidentRequestDTO`, `FeeResponseDTO`    |
| **Repository**     | PascalCase + hậu tố `Repository`       | `ResidentRepository`                      |
| **Mapper**         | PascalCase + hậu tố `Mapper`           | `ResidentMapper`                          |
| **Resource**       | PascalCase + hậu tố `Resource`         | `ResidentResource`                        |
| **Service**        | PascalCase + hậu tố `Service`          | `ResidentService`                         |
| **Event**          | PascalCase + hậu tố `Event`            | `ResidentCreatedEvent`, `FeeUpdatedEvent` |
| **Constant class** | PascalCase + hậu tố `Constants`        | `AppConstants`, `ErrorMessagesConstants`  |


### Biến

| Thành phần               | Quy ước                                                                   | Ví dụ                                                        |
|--------------------------|---------------------------------------------------------------------------|--------------------------------------------------------------|
| **Biến thường**          | camelCase                                                                 | `fullName`, `emailAddress`                                   |
| **Biến hằng (constant)** | UPPER_SNAKE_CASE + `static final`                                         | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`                       |
| **Biến boolean**         | bắt đầu bằng `is`, `has`, `can`, `should`                                 | `isActive`, `hasPermission`, `canCreate`                     |
| **Biến trong DTO**       | camelCase, trùng với JSON key (hoặc dùng `@JsonbProperty`)                | `fullName` ↔ `full_name`                                     |
| **Biến trong Entity**    | camelCase, phản ánh đúng cột DB (dùng `@Column(name="...")` khi khác tên) | `createdAt`, `updatedAt`                                     |
| **Biến trong Resource**  | đặt tên DTO theo `xxxRequest`, `xxxResponse`                              | `ResidentRequestDTO request`, `ResidentResponseDTO response` |
| **Biến trong Service**   | giữ nguyên tên `entity`, `dto`, `result`, `repository`                    | `apartmentRepository`, `residentEntity`                      |
| **Biến trong Mapper**    | dùng `dto` và `entity` để phân biệt rõ ràng                               | `toEntity(dto)`, `toDTO(entity)`                             |



### Hàm

| Thành phần                   | Quy ước                                   | Ví dụ                                                                        |
|------------------------------|-------------------------------------------|------------------------------------------------------------------------------|
| **Hàm thường**               | camelCase, động từ + danh từ              | `createResident()`, `getAllApartments()`, `calculateMonthlyFee()`            |
| **Getter / Setter**          | `getXxx()` / `setXxx()`                   | `getEmail()`, `setPhoneNumber()`                                             |
| **Hàm boolean**              | bắt đầu bằng `is`, `has`, `can`, `should` | `isDeleted()`, `hasAccess()`, `canUpdate()`                                  |
| **Hàm Repository (Panache)** | đặt theo hành động truy vấn               | `findByEmail()`, `findByApartmentId()`, `deleteByResidentId()`               |
| **Hàm Resource (REST)**      | trùng với HTTP action                     | `getResidents()`, `createResident()`, `updateResident()`, `deleteResident()` |
| **Hàm Service**              | trùng với tên hàm của Resource            | `getResidents()`, `createResident()`, `updateResident()`, `deleteResident()` |
| **Hàm Mapper**               | `toEntity()` / `toDTO()`                  | `toEntity(dto)`, `toDTO(entity)`                                             |

## 6. Triển khai Test

Tạo package resource, service, repository, triển khai Test cho toàn bộ các tầng tương ứng.
- Với resource: Test hết các hàm 
- Với service: chỉ test hàm nặng business logic, còn lại (với các luồng gọi thẳng resource -> service -> repository một lượt, không cần tổng hợp) thì không cần test
- Với repository: chỉ test hàm phức tạp, các hàm đơn giản như CRUD hay đã có trong PanacheRepository thì không cần.

Với mỗi phần Test cần 2 file đuôi Test và IT, chỉ Test trong file Test, file đuôi IT kế thừa Test là được.