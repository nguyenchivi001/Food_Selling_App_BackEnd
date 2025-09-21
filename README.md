# Food Selling App Backend 🍔

Dự án backend cho ứng dụng bán đồ ăn trực tuyến được xây dựng bằng Spring Boot, sử dụng JWT authentication và MySQL.

## Nhóm phát triển

- **Nguyễn Chí Vĩ** - 22521656
- **Dương Văn Súa** - 22521267

## Mô tả dự án

Food Selling App Backend là hệ thống backend RESTful API được phát triển bằng Spring Boot để hỗ trợ ứng dụng đặt đồ ăn trực tuyến. Dự án cung cấp các API để quản lý người dùng, danh mục món ăn, giỏ hàng, đơn hàng và các tính năng bảo mật với JWT authentication.

## Tính năng chính

### Authentication & Security
- JWT Authentication và Authorization
- Role-based access control
- Refresh Token mechanism
- Password encryption với BCrypt

### User Management
- Đăng ký/Đăng nhập người dùng
- Quản lý thông tin cá nhân
- Custom User Details Service

### Food Management
- CRUD operations cho món ăn
- Quản lý danh mục món ăn (Category)
- Food Service với business logic

### Cart & Favorites
- Quản lý giỏ hàng
- Danh sách món ăn yêu thích
- Cart Service với tính toán tổng tiền

### Order Management
- Tạo và theo dõi đơn hàng
- Quản lý trạng thái đơn hàng
- Order Service với workflow logic

## 🛠️ Công nghệ sử dụng

### Backend Framework
- **Spring Boot 3.x**
- **Spring Security 6.x** - Authentication & Authorization
- **Spring Data JPA** - Database ORM
- **Spring Web** - RESTful API

### Database
- **MySQL** - Primary Database
- **Spring Data JPA** - ORM Framework
- **Hibernate** - JPA Implementation

### Security
- **JWT (JSON Web Token)** - Authentication
- **Spring Security** - Security Framework
- **BCrypt** - Password Hashing

### Documentation & Testing
- **Spring Boot Test** - Unit & Integration Testing
- **JUnit 5** - Testing Framework
- **Mockito** - Mocking Framework

## Cài đặt và chạy dự án

### Yêu cầu hệ thống
- **Java 21** hoặc cao hơn
- **Maven 3.6+**
- **MySQL 8.0**
- **IDE**: IntelliJ IDEA

### Bước 1: Clone repository
```bash
git clone https://github.com/nguyenchivi001/Food_Selling_App_BackEnd.git
cd Food_Selling_App_BackEnd
```

### Bước 2: Cấu hình Database
#### Tạo database
```sql
-- MySQL
CREATE DATABASE food_selling_app;
```

### Bước 3: Build và chạy ứng dụng

## API Documentation

Base URL: `http://localhost:8080/api`

### Authentication Endpoints
```http
POST /api/auth/register          # Đăng ký
POST /api/auth/login          # Đăng nhập
POST /api/auth/refresh   # Refresh JWT token
POST /api/auth/logout         # Đăng xuất
```

### User Management
```http
GET    /api/users/me                  # Lấy thông tin profile
```

### Food Management
```http
GET /api/foods                        # Lấy danh sách món ăn (có phân trang, sort)
GET /api/foods/{id}                   # Lấy thông tin chi tiết món ăn theo id
POST /api/foods                       # Thêm món ăn mới
PUT /api/foods/{id}                   # Cập nhật món ăn theo id
DELETE /api/foods/{id}                # Xóa món ăn theo id

GET /api/foods/search                 # Tìm kiếm món ăn theo tên, categoryId (có phân trang)

POST /api/foods/{id}/image            # Upload ảnh cho món ăn theo id
GET /api/foods/images/{filename}      # Lấy ảnh món ăn theo tên file
```

### Cart Management
```http
GET /api/cart-items                   # Lấy danh sách món trong giỏ hàng
POST /api/cart-items                  # Thêm món vào giỏ hàng
PUT /api/cart-items/{foodId}          # Cập nhật số lượng món trong giỏ
DELETE /api/cart-items/{foodId}       # Xóa một món ra khỏi giỏ
DELETE /api/cart-items                # Xóa toàn bộ giỏ hàng
```

### Category Management
```http
GET    /api/categories                # Lấy danh sách categories
```

### Order Management
```http
POST /api/orders                      # Tạo đơn hàng mới
GET /api/orders                       # Lấy tất cả đơn hàng
GET /api/orders/search                # Tìm kiếm đơn hàng theo userId, name, status
PUT /api/orders/{id}/status           # Cập nhật trạng thái đơn hàng
DELETE /api/orders/{id}               # Xóa đơn hàng theo id
```

### Favorites
```http
GET /api/favorite-items               # Lấy danh sách món ăn yêu thích
POST /api/favorite-items              # Thêm món ăn vào danh sách yêu thích
DELETE /api/favorite-items/{foodId}   # Xóa một món ăn khỏi danh sách yêu thích
```

## Security Features

### JWT Authentication
- Access Token (24h expiry)
- Refresh Token (7 days expiry)
- Role-based authorization

### Endpoint Security
- Public endpoints: `/api/auth/**`
- User endpoints: Require USER role
- Admin endpoints: Require ADMIN role

### Password Security
- BCrypt hashing
- Strong password validation

