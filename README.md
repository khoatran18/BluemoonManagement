# ProjectIT3180

## 1. Khởi tạo database
```bash
cd tools/docker
docker compose -f docker_compose.yml up -d
```
Khi muốn xóa:
```bash
docker compose -f docker_compose.yml down -v
```

## 2. Chạy project
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
