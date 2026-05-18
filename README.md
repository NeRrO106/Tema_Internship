# Document Management Platform

Internal platform for document storage, task management, and team collaboration with role-based access control, MinIO integration, and comprehensive audit logging.

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.8+
- PostgreSQL 15 (via Docker)
- MinIO (via Docker)

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/document-management.git
cd document-management
```

2. **Create `.env` file**
```bash
cp .env.example .env
# Copy the .env file
```

3. **Start Docker services**
```bash
docker-compose up -d
```

4. **Build & Run**
```bash
mvn clean install
mvn spring-boot:run
```

5. **Access the application**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- MinIO Console: http://localhost:9001 (user: minioadmin, pass: minioadmin123)
- PostgreSQL: localhost:5432 (user: admin, pass: admin123)

---
## API Documentation

### Authentication

All endpoints (except `/api/auth/**`) require JWT authentication.

**Login endpoint:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "username": "username",
  "role": "USER"
}
```

**Register endpoint:**
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "username": "newuser",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "newuser@example.com",
  "username": "newuser",
  "role": "USER"
}
```
**Login 2fa endpoint:**
```bash
POST /api/auth/login/verify-2fa?email={email@example.com}
Content-Type: application/json

{
  {"totpCode": "123456"}
}

Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "Admin",
    "role": "ADMIN",
    "twoFactorEnabled": true,
    "twoFactorRequired": false,
    "temporaryToken": null
}
```
---

## Endpoints

### Users
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/api/users/me` | ✅ | ANY | Get current user profile |
| PUT | `/api/users/me` | ✅ | ANY | Update profile |
| GET | `/api/users` | ✅ | ADMIN | List all users |
| PUT | `/api/users/{id}/role` | ✅ | ADMIN | Change user role |
| PUT | `/api/users/{id}/deactivate` | ✅ | ADMIN | Deactivate user |
| PUT | `/api/users/{id}/activate` | ✅ | ADMIN | Activate user |

### Projects
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/api/projects` | ✅ | ANY | Create project |
| GET | `/api/projects` | ✅ | ADMIN | List all projects |
| GET | `/api/projects/my` | ✅ | ANY | List user's projects |
| GET | `/api/projects/{id}` | ✅ | ANY | Get project details |
| PUT | `/api/projects/{id}` | ✅ | OWNER | Update project |
| DELETE | `/api/projects/{id}` | ✅ | OWNER | Soft delete project |
| POST | `/api/projects/{id}/members/{userId}` | ✅ | OWNER | Add member |
| DELETE | `/api/projects/{id}/members/{userId}` | ✅ | OWNER | Remove member |

### Tasks
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/api/projects/{projectId}/tasks` | ✅ | ANY | Create task |
| GET | `/api/projects/{projectId}/tasks` | ✅ | ANY | List project tasks |
| GET | `/api/projects/{projectId}/tasks?status=TODO` | ✅ | ANY | Filter by status |
| GET | `/api/projects/{projectId}/tasks?priority=HIGH` | ✅ | ANY | Filter by priority |
| GET | `/api/projects/{projectId}/tasks/{taskId}` | ✅ | ANY | Get task details |
| PUT | `/api/projects/{projectId}/tasks/{taskId}` | ✅ | MEMBER | Update task |
| DELETE | `/api/projects/{projectId}/tasks/{taskId}` | ✅ | MEMBER | Delete task |
| POST | `/api/projects/{projectId}/tasks/{taskId}/assign/{userId}` | ✅ | MEMBER | Assign task |
| POST | `/api/projects/{projectId}/tasks/{taskId}/unassign` | ✅ | ASSIGNEE | Unassign self |

### Documents
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/api/projects/{projectId}/documents` | ✅ | ANY | Upload document |
| GET | `/api/projects/{projectId}/documents` | ✅ | ANY | List documents |
| GET | `/api/projects/{projectId}/documents/{docId}` | ✅ | ANY | Get document info |
| GET | `/api/projects/{projectId}/documents/{docId}/download` | ✅ | ANY | Download file |
| DELETE | `/api/projects/{projectId}/documents/{docId}` | ✅ | ANY | Delete document |

### Audit Logs (ADMIN ONLY)
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/api/audit-logs` | ✅ | ADMIN | Get all audit logs |
| GET | `/api/audit-logs/user/{userId}` | ✅ | ADMIN | Get user audit logs |
| GET | `/api/audit-logs/action/{action}` | ✅ | ADMIN | Get logs by action |

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'USER',
  is_active BOOLEAN DEFAULT true,
  totp_secret VARCHAR(255),
  two_factor_enabled BOOLEAN DEFAULT false,
  two_factor_verified BOOLEAN DEFAULT false,
  two_factor_setup_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Projects Table
```sql
CREATE TABLE projects (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  status VARCHAR(50) DEFAULT 'ACTIVE',
  owner_id BIGINT NOT NULL REFERENCES users(id),
  deleted_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  priority VARCHAR(20) DEFAULT 'MEDIUM',
  status VARCHAR(20) DEFAULT 'TODO',
  deadline TIMESTAMP,
  project_id BIGINT NOT NULL REFERENCES projects(id),
  assigned_to BIGINT REFERENCES users(id),
  created_by BIGINT NOT NULL REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Documents Table
```sql
CREATE TABLE documents (
  id BIGSERIAL PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_size BIGINT NOT NULL,
  mime_type VARCHAR(100),
  storage_path VARCHAR(500) NOT NULL,
  file_hash VARCHAR(64),
  project_id BIGINT NOT NULL REFERENCES projects(id),
  uploaded_by BIGINT NOT NULL REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Audit Logs Table
```sql
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  action VARCHAR(100) NOT NULL,
  resource_type VARCHAR(100) NOT NULL,
  resource_id BIGINT,
  ip_address VARCHAR(45),
  user_agent TEXT,
  reason VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Security**: Spring Security + JWT (JJWT 0.11.5)
- **Database**: PostgreSQL 15
- **ORM**: Hibernate 7.2.12 + JPA
- **Migrations**: Flyway 11.14.1
- **Storage**: MinIO 8.5.14
- **API Docs**: SpringDoc OpenAPI 2.5.0
- **Build**: Maven 3.8+
- **Container**: Docker + Docker Compose

### GitHub Repository Structure

```
document-management/
├── src/
│   ├── main/
│   │   ├── java/com/internship/documentmanagement/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── security/
│   │   │   ├── exception/
│   │   │   ├── config/
│   │   │   ├── util/
│   │   │   └── DocumentManagementApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_projects_table.sql
│   │           ├── V3__create_tasks_table.sql
│   │           ├── V4__create_documents_table.sql
│   │           └── V5__create_audit_logs_table.sql
│   └── test/
├── .gitignore
├── .env.example
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── README.md
└── POSTMAN_COLLECTION.json
```

---
## Postman Collection
Salvat in `POSTMAN_COLLECTION.json`

## Security Hardening(for frontend)

### CORS Configuration

```java
// In SecurityConfig
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",
        "http://localhost:5173"
    ));
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"
    ));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```
## Deployment Guide

### Local Docker Deployment

```bash
# 1. Build Docker image
docker build -t document-management:latest .

# 2. Run with Docker Compose
docker-compose up -d

# 3. Verify services
docker-compose ps

# 4. Check logs
docker-compose logs -f app

# 5. Access application
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui/index.html
# MinIO: http://localhost:9001
```

### Heroku Deployment (Optional)

```bash
# 1. Install Heroku CLI
brew tap heroku/brew && brew install heroku

# 2. Login
heroku login

# 3. Create app
heroku create document-management

# 4. Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# 5. Deploy
git push heroku main

# 6. View logs
heroku logs --tail
```

### AWS EC2 Deployment (Optional)

```bash
# 1. Launch EC2 instance (Ubuntu 22.04)
# 2. SSH into instance
ssh -i key.pem ubuntu@your-ec2-ip

# 3. Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 4. Clone repository
git clone https://github.com/yourusername/document-management.git
cd document-management

# 5. Start services
docker-compose up -d

# 6. Configure Nginx reverse proxy (optional)
```