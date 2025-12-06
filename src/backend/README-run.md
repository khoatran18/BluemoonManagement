# Hướng dẫn chạy và test

## 1. Triển khai database
**Di chuyển vào folder chứa Docker compose file**
```bash
docker compose -f docker_compose.yml up -d
```

## 2. Build dự án
**Di chuyển vào folder backend**

```bash
./mvnw clean install
```

## 3. Chạy mock data

**Sau khi build thành công, chạy file MockData trong**

```
ProjectIT3180\src\backend\common-package\src\main\java\com\project\common_package\tools\MockData.java
```