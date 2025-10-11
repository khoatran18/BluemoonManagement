# ProjectIT3180

## 1. Khởi tạo database
```bash
cd tools/docker
docker compose -f init_db.yml up -d
```
Khi muốn xóa:
```bash
docker compose -f init_db.yml down -v
```