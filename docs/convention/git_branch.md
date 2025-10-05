# Git Branch

#### Lưu ý: 
- Check  out branch nào thì push branch đó, không merge vào các branch chung như main, develop (việc này dùng pull request).
- Luôn dùng ```git status``` kiểm tra xem đã đúng branch chưa trước khi push.


## I. Cấu trúc nhánh

```markdown
main ← chứa code stable (release)
│
├── develop ← code đang phát triển (chuẩn bị release)
│ │
│ ├── feature/* ← chức năng theo sprint
│ ├── bugfix/* ← sửa lỗi trong develop
│
└── hotfix/* ← sửa lỗi khẩn từ main, merge lại main + develop
└── docs/* ← cập nhật docs
```

---

## II. Quy tắc dùng branch

| Nhánh       | Tạo từ | Merge vào | Dùng khi                              |
|-------------|--------|------------|---------------------------------------|
| `feature/*` | develop | develop | Phát triển tính năng trong sprint     |
| `bugfix/*`  | develop | develop | Sửa lỗi khi code đã merge vào develop |
| `release/*` | develop | main + develop | Chuẩn bị phát hành                    |
| `hotfix/*`  | main | main | Sửa lỗi khẩn trên production          |
| `docs/*`    | main | main | Cập nhật tài liệu                     |

---

## III. Luồng làm việc chuẩn

### 1. Tạo branch mới từ develop
```bash
git checkout develop
git pull origin develop
git checkout -b feature/sprint_number-feature_name
```
**Ví dụ:** git checkout -b feature/sprint1-login

### 2. Commit, Push branch
```bash
git add .
git commit -m "feat(auth): implement login feature"
git push origin feature/sprint_number-feature_name
```
**Ví dụ:** git push origin feature/sprint1-login

### 3. Pull request vào develop, và test
Sau khi các tính năng đã xong (trên các feature/*) thì sẽ vào các nhánh, tạo pull request đến develop.\
Xử lý conflict và test.\
Nếu có lỗi, kéo code về máy:
```bash
git checkout develop
git pull origin develop
git checkout -b bugfix/sprint_number-feature_name
# Fix bug
git add .
git commit -m "fix(...): ..."
git push origin bugfix/sprint_number-feature_name
```
**Ví dụ:**  
git checkout -b bugfix/sprint1-fix_login_error\
git commit -m "fix(auth): handle login token error"\
git push origin hotfix/fix-login-error\
\
Sau đó, push lên, pull request lại vào develop.

### 4. Pull request từ develop vào main
Sau khi xong 1 Sprint, merge thành công các feature vào develop, fix bug xong thì tạo pull request vào main.\
Trường hợp main chạy có lỗi, tạo 1 branch từ main và sửa, push nhánh đó lên và pull request vào main.\
```bash
git checkout main
git pull origin main
git checkout -b hotfix/bug_name
```
Fix bug,  commit
```bash
git add .
git commit -m "fix(...): ..."
git push origin hotfix/bug_name
```
Fix bug, commit lên và pull request lại vào main.\
Sau khi xong, 1 thành viên đồng bộ main vào develop:
```bash
git checkout develop
git pull origin main
git push origin develop
```
Các thành viên còn lại câp nhật lại branch develop và branch feature đang thực hiện trên máy:
* Đầu tiên: commit đoạn code đang làm
```bash
git add .
git commit -m ""
```
* Tiến hành cập nhật:
```bash
git checkout develop
git pull origin develop
git checkout feature/nhanh_dang_thuc_hien
git merge develop
```

### 5. Cập nhật docs
```bash
git checkout main
git pull origin main
git checkout -b docs/...
```
Cập nhật docs
```bash
git add .
git commit -m "docs: ..."
git push origin docs/...
```
Sau đó, 1 thành viên kiểm tra và pull request vào main