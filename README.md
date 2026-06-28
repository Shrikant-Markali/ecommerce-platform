# 🛒 ECommerce Platform — Project Blueprint

> A production-ready, full-stack e-commerce platform built with Java Spring Boot Microservices and React.js, featuring PayPal payment integration.

---

## 📌 Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Services & Ports](#services--ports)
5. [Features](#features)
6. [API Endpoints](#api-endpoints)
7. [Database Design](#database-design)
8. [Security Standards](#security-standards)
9. [API Response Format](#api-response-format)
10. [Project Standards](#project-standards)
11. [Development Phases](#development-phases)
12. [Folder Structure](#folder-structure)

---

## 📖 Project Overview

A fully functional, scalable e-commerce platform with PayPal payment integration built using industry-standard practices. The platform follows a microservices architecture where each service is independently deployable, has its own database, and communicates via REST (synchronous) or RabbitMQ (asynchronous).

**Key Highlights:**
- 🏗️ Microservices Architecture
- 🔐 JWT Authentication with Refresh Tokens
- 💳 PayPal Sandbox Payment Integration
- 📧 Email Notifications via RabbitMQ
- 📊 Admin Dashboard with Analytics
- 🚀 CI/CD Pipeline with GitHub Actions
- 📝 Swagger API Documentation
- ✅ Unit + Integration Tests

---

## 🛠️ Tech Stack

### Backend

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.x |
| Architecture | Microservices | - |
| Service Discovery | Eureka Server | Spring Cloud |
| API Gateway | Spring Cloud Gateway | Spring Cloud |
| Inter-service Communication | OpenFeign | Spring Cloud |
| Fault Tolerance | resilience4j | Latest |
| Message Broker | RabbitMQ | Latest |
| Security | Spring Security + JWT | Latest |
| Password Hashing | BCrypt | - |
| ORM | Spring Data JPA + Hibernate | Latest |
| Database Migration | Flyway | Latest |
| Documentation | Swagger / OpenAPI 3 | Latest |
| Testing | JUnit 5 + Mockito | Latest |
| Logging | SLF4J + Logback | Latest |
| Monitoring | Spring Actuator | Latest |
| Build Tool | Maven | 3.9.9 |

### Frontend

| Layer | Technology | Version |
|---|---|---|
| Framework | React.js | 18.x |
| Build Tool | Vite | Latest |
| Styling | Tailwind CSS | 3.x |
| State Management | Redux Toolkit | Latest |
| Routing | React Router | v6 |
| API Calls | Axios | Latest |
| UI Components | ShadCN UI | Latest |
| Form Handling | React Hook Form | Latest |
| Validation | Zod | Latest |
| Icons | Lucide React | Latest |

### Database

| Service | Database |
|---|---|
| User Service | MySQL — `users_db` |
| Product Service | MySQL — `products_db` |
| Order Service | MySQL — `orders_db` |
| Payment Service | MySQL — `payments_db` |

### DevOps

| Tool | Purpose |
|---|---|
| Git + GitHub | Version control |
| GitHub Actions | CI/CD Pipeline |
| Vercel | Frontend hosting |
| Railway / Render | Backend hosting |
| Postman | API testing |

---

## 🏗️ Architecture

```
                        [ Client ]
                   React.js (Port 3000)
                           │
                    [ API Gateway ]
                   Spring Cloud (8080)
                           │
              ┌────────────┼────────────┐
              │            │            │
       [User Service]  [Product]   [Order Service]
          (8081)       Service        (8083)
              │          (8082)          │
              └────────────┼────────────┘
                           │
                  [Payment Service]
                     PayPal (8084)
                           │
                 [Notification Service]
                    RabbitMQ (8085)
                           │
              ┌────────────┴────────────┐
              │                         │
       [Eureka Server]             [MySQL DBs]
          (8761)                    (3306)
```

### Communication Types

**Synchronous (REST via OpenFeign):**
- Order Service → Product Service (check stock & price)
- Order Service → Payment Service (initiate payment)

**Asynchronous (RabbitMQ):**
- Payment Service → Notification Service (payment confirmation email)
- Order Service → Notification Service (order placed email)

---

## 🖥️ Services & Ports

| Service | Port | Database |
|---|---|---|
| Eureka Server | 8761 | None |
| API Gateway | 8080 | None |
| User Service | 8081 | users_db |
| Product Service | 8082 | products_db |
| Order Service | 8083 | orders_db |
| Payment Service | 8084 | payments_db |
| Notification Service | 8085 | None |
| MySQL | 3306 | - |
| RabbitMQ | 5672 | - |
| React Frontend | 3000 | - |

---

## 🌟 Features

### 👤 Customer Features

| # | Feature |
|---|---|
| 1 | User Registration |
| 2 | User Login with JWT |
| 3 | JWT Refresh Token |
| 4 | View & Update Profile |
| 5 | Change Password |
| 6 | Forgot Password (Email OTP) |
| 7 | Browse Products |
| 8 | Search Products |
| 9 | Filter by Category, Price, Rating |
| 10 | View Product Details |
| 11 | Add to Cart |
| 12 | Remove from Cart |
| 13 | Update Cart Quantity |
| 14 | Add to Wishlist |
| 15 | Place Order |
| 16 | Pay with PayPal |
| 17 | View Order History |
| 18 | Track Order Status |
| 19 | Cancel Order |
| 20 | Receive Email Confirmation |

### 🔐 Admin Features

| # | Feature |
|---|---|
| 1 | Admin Login |
| 2 | Dashboard with Stats |
| 3 | Add Product |
| 4 | Edit Product |
| 5 | Delete Product (Soft) |
| 6 | Manage Categories |
| 7 | View All Orders |
| 8 | Update Order Status |
| 9 | View All Users |
| 10 | Enable / Disable User |
| 11 | View Payment Reports |
| 12 | View Sales Analytics |

---

## 🔌 API Endpoints

### User Service (Port 8081)

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/auth/register` | Public |
| POST | `/api/v1/auth/login` | Public |
| POST | `/api/v1/auth/refresh` | Public |
| POST | `/api/v1/auth/logout` | Auth |
| POST | `/api/v1/auth/forgot-password` | Public |
| POST | `/api/v1/auth/reset-password` | Public |
| GET | `/api/v1/users/profile` | Auth |
| PUT | `/api/v1/users/profile` | Auth |
| PUT | `/api/v1/users/change-password` | Auth |
| GET | `/api/v1/admin/users` | Admin |
| PUT | `/api/v1/admin/users/{id}/status` | Admin |

### Product Service (Port 8082)

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/products` | Public |
| GET | `/api/v1/products/{id}` | Public |
| GET | `/api/v1/products/search` | Public |
| GET | `/api/v1/products/category/{id}` | Public |
| POST | `/api/v1/admin/products` | Admin |
| PUT | `/api/v1/admin/products/{id}` | Admin |
| DELETE | `/api/v1/admin/products/{id}` | Admin |
| GET | `/api/v1/categories` | Public |
| POST | `/api/v1/admin/categories` | Admin |
| PUT | `/api/v1/admin/categories/{id}` | Admin |

### Order Service (Port 8083)

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/cart` | Auth |
| POST | `/api/v1/cart/add` | Auth |
| PUT | `/api/v1/cart/update` | Auth |
| DELETE | `/api/v1/cart/remove/{id}` | Auth |
| DELETE | `/api/v1/cart/clear` | Auth |
| POST | `/api/v1/orders` | Auth |
| GET | `/api/v1/orders` | Auth |
| GET | `/api/v1/orders/{id}` | Auth |
| DELETE | `/api/v1/orders/{id}/cancel` | Auth |
| GET | `/api/v1/admin/orders` | Admin |
| PUT | `/api/v1/admin/orders/{id}/status` | Admin |

### Payment Service (Port 8084)

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/payments/create` | Auth |
| GET | `/api/v1/payments/success` | Auth |
| GET | `/api/v1/payments/cancel` | Auth |
| GET | `/api/v1/payments/{orderId}` | Auth |
| GET | `/api/v1/admin/payments` | Admin |

### Notification Service (Port 8085)

| Note | Detail |
|---|---|
| Communication | Internal RabbitMQ only |
| REST APIs | None (event-driven) |

---

## 🗄️ Database Design

### users_db

```sql
-- users table
CREATE TABLE users (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(15),
    is_enabled    BOOLEAN DEFAULT TRUE,
    is_deleted    BOOLEAN DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

-- roles table
CREATE TABLE roles (
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    name  ENUM('USER', 'ADMIN') NOT NULL
);

-- user_roles table
CREATE TABLE user_roles (
    user_id  BIGINT,
    role_id  BIGINT,
    PRIMARY KEY (user_id, role_id)
);

-- refresh_tokens table
CREATE TABLE refresh_tokens (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    token        VARCHAR(255) UNIQUE NOT NULL,
    user_id      BIGINT NOT NULL,
    expiry_date  TIMESTAMP NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- addresses table
CREATE TABLE addresses (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    street      VARCHAR(255),
    city        VARCHAR(100),
    state       VARCHAR(100),
    pincode     VARCHAR(10),
    is_default  BOOLEAN DEFAULT FALSE,
    is_deleted  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### products_db

```sql
-- categories table
CREATE TABLE categories (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    image_url    VARCHAR(500),
    is_deleted   BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100)
);

-- products table
CREATE TABLE products (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    price           DECIMAL(10,2) NOT NULL,
    stock_quantity  INT DEFAULT 0,
    image_url       VARCHAR(500),
    category_id     BIGINT NOT NULL,
    rating          DECIMAL(3,2) DEFAULT 0.00,
    is_deleted      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);
```

### orders_db

```sql
-- carts table
CREATE TABLE carts (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- cart_items table
CREATE TABLE cart_items (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL
);

-- orders table
CREATE TABLE orders (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    total_amount  DECIMAL(10,2) NOT NULL,
    status        ENUM('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    address_id    BIGINT,
    is_deleted    BOOLEAN DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

-- order_items table
CREATE TABLE order_items (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id      BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    product_name  VARCHAR(255),
    quantity      INT NOT NULL,
    price         DECIMAL(10,2) NOT NULL
);
```

### payments_db

```sql
-- payments table
CREATE TABLE payments (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id           BIGINT NOT NULL,
    user_id            BIGINT NOT NULL,
    paypal_order_id    VARCHAR(255),
    paypal_payment_id  VARCHAR(255),
    amount             DECIMAL(10,2) NOT NULL,
    currency           VARCHAR(10) DEFAULT 'USD',
    status             ENUM('PENDING','COMPLETED','FAILED','CANCELLED') DEFAULT 'PENDING',
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 🔐 Security Standards

| Standard | Implementation |
|---|---|
| Password Hashing | BCrypt |
| Authentication | JWT Access Token (15 min expiry) |
| Session Management | JWT Refresh Token (7 days expiry) |
| Authorization | Role-based (USER / ADMIN) |
| Data Encryption | AES-256 for sensitive data |
| API Protection | Rate limiting at Gateway |
| CORS | Configured at Gateway level |
| SQL Injection | JPA / Prepared Statements |
| XSS Protection | Input sanitization |
| HTTPS | SSL via hosting platform |

---

## 📦 API Response Format

Every API response follows this standard format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "errors": null,
  "timestamp": "2025-06-27T10:00:00",
  "path": "/api/v1/users/profile"
}
```

**Error Response:**

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": [
    "Email is required",
    "Password must be at least 8 characters"
  ],
  "timestamp": "2025-06-27T10:00:00",
  "path": "/api/v1/auth/register"
}
```

---

## 📋 Project Standards

### Every Database Table Has

```sql
created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
created_by  VARCHAR(100),
updated_by  VARCHAR(100),
is_deleted  BOOLEAN DEFAULT FALSE   -- Soft delete, never hard delete
```

### Code Quality
- Clean Architecture
- SOLID Principles
- Design Patterns
- Proper Exception Handling
- Input Validation everywhere
- Meaningful variable/method names
- Comments and Javadoc

### API Standards
- REST conventions
- Consistent response format
- Proper HTTP status codes
- Pagination on all list APIs
- Swagger documentation
- API versioning (`/api/v1/...`)

### JVM Optimization (8GB RAM)
Each service runs with limited heap to save memory:
```properties
# Add to each service's JVM args
-Xmx256m -Xms128m
```

---

## 📅 Development Phases

| Phase | Service | Key Tasks |
|---|---|---|
| 1 | **Eureka Server** | Service registry setup |
| 2 | **User Service** | Register, Login, JWT, Refresh Token |
| 3 | **API Gateway** | Routing, JWT validation, Rate limiting |
| 4 | **Product Service** | CRUD, Categories, Search, Filter |
| 5 | **Order Service** | Cart, Orders, Order tracking |
| 6 | **Payment Service** | PayPal integration, Payment flow |
| 7 | **Notification Service** | RabbitMQ, Email on events |
| 8 | **React Frontend** | All pages, Redux, PayPal button |
| 9 | **Testing** | Unit tests, Integration tests |
| 10 | **Deployment** | CI/CD, Railway, Vercel, Live URL |

---

## 📁 Folder Structure

```
ecommerce-platform/
│
├── backend/
│   ├── eureka-server/
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   └── pom.xml
│   │
│   ├── api-gateway/
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   └── pom.xml
│   │
│   ├── user-service/
│   │   ├── src/main/java/com/ecommerce/user/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── security/
│   │   │   ├── exception/
│   │   │   └── config/
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   ├── src/test/
│   │   └── pom.xml
│   │
│   ├── product-service/
│   │   └── (same structure as user-service)
│   │
│   ├── order-service/
│   │   └── (same structure as user-service)
│   │
│   ├── payment-service/
│   │   └── (same structure as user-service)
│   │
│   └── notification-service/
│       └── (same structure as user-service)
│
├── frontend/
│   └── react-app/
│       ├── src/
│       │   ├── components/
│       │   ├── pages/
│       │   ├── store/           (Redux)
│       │   ├── services/        (Axios API calls)
│       │   ├── hooks/
│       │   ├── utils/
│       │   └── App.jsx
│       ├── public/
│       ├── index.html
│       ├── vite.config.js
│       ├── tailwind.config.js
│       └── package.json
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml
│
└── README.md
```

---

## 💰 Cost Summary

| Item | Cost |
|---|---|
| All backend technologies | ✅ Free |
| All frontend technologies | ✅ Free |
| MySQL | ✅ Free |
| RabbitMQ (local) | ✅ Free |
| PayPal Sandbox | ✅ Free |
| GitHub + GitHub Actions | ✅ Free |
| Vercel (frontend hosting) | ✅ Free |
| Railway / Render (backend) | ✅ Free tier |
| **Total** | **₹0** |

---

## 👨‍💻 Developer

**Shrikant Markali**
- M.Sc Computer Science
- Java Spring Boot | Microservices | React.js
- GitHub: [your-github-link]
- LinkedIn: [your-linkedin-link]

---

> Built with ❤️ as a production-ready portfolio project
