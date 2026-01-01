-- 1. Nạp Master Data (Giữ nguyên ID vì các bảng khác tham chiếu đến)
INSERT INTO FeeType (FeeTypeID, FeeTypeName) VALUES (1, 'OBLIGATORY');
INSERT INTO FeeType (FeeTypeID, FeeTypeName) VALUES (2, 'IMPROMPTU');
INSERT INTO FeeType (FeeTypeID, FeeTypeName) VALUES (3, 'VOLUNTARY');

INSERT INTO FeeCategory (FeeCategoryID, FeeTypeID, FeeCategoryName, FeeCategoryDescription)
VALUES (1, 1, 'Tiền Điện', 'Phí điện hàng tháng');

-- Reset auto-increment to avoid PK clash with seeded FeeCategoryID=1
ALTER TABLE FeeCategory ALTER COLUMN FeeCategoryID RESTART WITH 2;

-- 2. KHÔNG chèn thủ công vào bảng Apartment và Fee nữa để tránh tranh chấp ID với code Test
-- Hoặc nếu muốn chèn, hãy dùng ID rất lớn (ví dụ: 999) để không bị clash
INSERT INTO Apartment (ApartmentID, Building, RoomNumber, CreatedAt) VALUES (999, 'A-Seed', '999', CURRENT_TIMESTAMP);
INSERT INTO Fee (FeeID, FeeName, FeeDescription, Amount, FeeCategoryID, FeeTypeID, Status, StartDate, CreatedAt)
VALUES (999, 'Phí Seed', 'Mô tả', 1000, 1, 1, 'ACTIVE', '2025-01-01', CURRENT_TIMESTAMP);

-- 3. Reset bộ đếm ID (Dành riêng cho H2 Database trong lúc test)
ALTER TABLE Apartment ALTER COLUMN ApartmentID RESTART WITH 1;
ALTER TABLE Fee ALTER COLUMN FeeID RESTART WITH 1;