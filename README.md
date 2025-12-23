# ProjectIT3180

## 1. Khởi tạo database


### Với Supabase

Vào Supabase, chọn connect bằng jdbc.\
Điền vào 2 file **application.properties** trong *auth-service* và *resident_fee-service*:
```angular2html
quarkus.datasource.jdbc.url=jdbc:postgresql://xxxxxxxxxxxxxx
```

### Với Docker
```bash
cd tools/docker
docker compose -f docker_compose.yml up -d
```
Khi muốn xóa:
```bash
docker compose -f docker_compose.yml down -v
```

## 2. Build project

**Truy cập folder backend**

```bash
./mvnw clean package
```

```bash
java -jar D:\IntelliJ\HUST\IT3180\ProjectIT3180\src\backend\resident_fee-service\target\quarkus-app\quarkus-run.jar
```
### Dùng khi lỗi timezone database: 
Vào Terminal (trực tiếp trong IntelliJ hoặc PowerShell có thể gây lỗi không nhận diện được biến), chạy:
```bash
 java -Duser.timezone=Asia/Ho_Chi_Minh -jar D:\IntelliJ\HUST\IT3180\ProjectIT3180\src\backend\resident_fee-service\target\quarkus-app\quarkus-run.jar
 ```

## 3. Chạy Project bằng Docker (Không cần bước 1, 2)

### Với Docker
```bash
cd tools/docker
docker compose -f docker_backend.yml up -d
```
Khi muốn xóa:
```bash
docker compose -f docker_backend.yml down -v
```
Truy cập ```localhost:8080```
