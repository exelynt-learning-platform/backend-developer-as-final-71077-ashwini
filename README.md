# Resource Booking System

Secure RESTful Resource Booking API using Spring Boot, Java 17+, Spring Security, JWT, JPA/Hibernate and MySQL.

## Features
- JWT login: `POST /auth/login`
- ADMIN and USER RBAC
- BCrypt password hashing
- USER can read resources, create reservations, and see only their own reservations
- ADMIN has full CRUD for resources and reservations
- Reservation statuses: PENDING, CONFIRMED, CANCELLED
- Decimal reservation prices
- Filtering by status/minPrice/maxPrice
- Pagination: page/size
- Optional sorting: `sort=price,asc`
- Overlap protection for PENDING/CONFIRMED bookings
- Bean validation and JSON error responses
- Swagger/OpenAPI
- Automatic seed users/resources

## Requirements
- JDK 17 or newer
- Maven 3.9+
- MySQL 8+
- Git/IDE optional

## 1. Create database
Run:
```sql
CREATE DATABASE resource_booking_db;
```

## 2. Configure environment variables
Windows CMD:
```bat
set DB_URL=jdbc:mysql://localhost:3306/resource_booking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=YOUR_MYSQL_PASSWORD
set JWT_SECRET=replace-with-a-random-secret-at-least-32-bytes-long
set SERVER_PORT=8080
```

PowerShell:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/resource_booking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:JWT_SECRET="replace-with-a-random-secret-at-least-32-bytes-long"
$env:SERVER_PORT="8080"
```

You can also edit `src/main/resources/application.properties`.

## 3. Run
```bash
mvn clean spring-boot:run
```
Or build:
```bash
mvn clean package
java -jar target/resource-booking-system-1.0.0.jar
```

## Seed credentials
ADMIN:
- email: `admin@example.com`
- password: `Admin@123`

USER:
- email: `user@example.com`
- password: `User@123`

Change these credentials before production use.

## Swagger
Open:
`http://localhost:8080/swagger-ui.html`

OpenAPI JSON:
`http://localhost:8080/v3/api-docs`

## API summary

### Authentication
`POST /auth/login`
```json
{
  "email": "user@example.com",
  "password": "User@123"
}
```
Copy the returned token and send:
`Authorization: Bearer <token>`

### Resources
USER + ADMIN:
- `GET /resources`
- `GET /resources/{id}`

ADMIN only:
- `POST /resources`
- `PUT /resources/{id}`
- `DELETE /resources/{id}`

Create/update body:
```json
{
  "name": "Meeting Room",
  "type": "ROOM",
  "description": "Large meeting room",
  "pricePerBooking": 300.00,
  "available": true
}
```

### USER reservation creation
`POST /reservations`
```json
{
  "resourceId": 1,
  "price": 300.00,
  "startTime": "2026-10-01T10:00:00",
  "endTime": "2026-10-01T12:00:00"
}
```
There is deliberately **no userId** in this request. The user is taken from the authenticated JWT.

### Reservations
USER:
- `POST /reservations`
- `GET /reservations`
- `GET /reservations/{id}` (only own reservation)

ADMIN:
- all USER operations
- `POST /reservations/admin`
- `PUT /reservations/{id}`
- `DELETE /reservations/{id}`
- `GET /reservations` returns all reservations for ADMIN

Filtering/pagination example:
`GET /reservations?page=0&size=10&status=CONFIRMED&minPrice=100&maxPrice=1000&sort=price,asc`

Allowed sort fields: `id`, `price`, `startTime`, `endTime`, `status`.

## Security behavior
- No session is used; authentication is stateless JWT.
- Passwords are stored using BCrypt.
- USER cannot create/update/delete resources.
- USER cannot access another user's reservation even if they know its ID.
- USER cannot submit a userId to impersonate another account.
- ADMIN can access all reservations and resource CRUD endpoints.

## Project structure
```text
src/main/java/com/example/booking
├── config
│   └── DataSeeder.java
├── controller
│   ├── AuthController.java
│   ├── ResourceController.java
│   └── ReservationController.java
├── dto
├── entity
├── exception
├── repository
├── security
├── service
└── ResourceBookingApplication.java
```

## Notes
`spring.jpa.hibernate.ddl-auto=update` is convenient for the assignment. For production, use Flyway/Liquibase migrations and set `ddl-auto=validate`.
