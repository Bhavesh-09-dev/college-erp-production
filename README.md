# 🎓 Smart College ERP Portal

A complete, production-ready College ERP system built with **Java 23**, **Spring Boot 3.3**, **MySQL**, **Spring Security**, **Spring Data JPA/Hibernate**, **Thymeleaf**, and **Bootstrap 5**.

---

## 📋 Features

### 1. Authentication & Authorization
- Role-based login: **ADMIN**, **FACULTY**, **STUDENT**
- BCrypt password encryption
- Spring Security form login with role-based redirects
- Secure logout, session management

### 2. Student Management (Admin)
- Add / Update / Delete (soft-delete) students
- Search by name, enrollment number, email, department
- Paginated student listing
- Full student profile with attendance & marks summary

### 3. Faculty Management (Admin)
- Complete CRUD operations
- Faculty dashboard with quick actions
- Search and pagination

### 4. Attendance Management (Faculty)
- Mark attendance per subject/class (single or bulk)
- Attendance reports by department & semester
- Automatic attendance percentage calculation
- Subject-wise attendance breakdown

### 5. Marks & Results Management (Faculty/Admin)
- Upload and edit marks (MID_TERM, END_TERM, etc.)
- Automatic grade calculation (O, A+, A, B+, B, C, F)
- Semester result generation (PASS/FAIL + grade)
- Subject-wise performance charts

### 6. Notice Board (Admin)
- Create / update / delete notices
- Priority levels (LOW, NORMAL, HIGH, URGENT)
- Target audience (ALL, STUDENTS, FACULTY, DEPARTMENT)
- Student & faculty notice dashboards

### 7. Analytics Dashboard (Admin)
- Total students / faculty counts
- Attendance statistics & at-risk student detection
- Marks statistics & grade distribution
- Chart.js visualizations: bar, doughnut, line, radar charts

### 8. AI Features (Pure Java Algorithms - No External APIs)
- **Attendance risk detection**: students below 75% flagged automatically
- **Risk classification**: LOW / MEDIUM / HIGH / CRITICAL based on attendance + recent absences
- **Performance prediction**: weighted score (40% attendance + 60% marks)
- **Grade prediction**: predictive final grade estimation
- **Trend analysis**: IMPROVING / STABLE / DECLINING for attendance and marks
- **Personalized suggestions**: actionable recommendations per student
- **Warnings**: critical alerts for exam-barring attendance or failing marks

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 23 |
| Framework | Spring Boot 3.3.4 |
| Build Tool | Maven |
| Database | MySQL 8+ |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 6 (BCrypt, RBAC) |
| Templating | Thymeleaf + Thymeleaf Security extras |
| Frontend | Bootstrap 5, Bootstrap Icons, Chart.js, vanilla JS |
| Boilerplate | Lombok |
| Validation | Jakarta Bean Validation |

---

## 📂 Project Structure

```
college-erp/
├── pom.xml
├── sample-data.sql
├── README.md
└── src/main/
    ├── java/com/college/erp/
    │   ├── CollegeErpApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   └── DataInitializer.java
    │   ├── controller/
    │   │   ├── AuthController.java
    │   │   ├── AdminController.java
    │   │   ├── FacultyController.java
    │   │   ├── StudentController.java
    │   │   └── ErrorPageController.java
    │   ├── dto/
    │   │   ├── StudentDTO.java
    │   │   ├── FacultyDTO.java
    │   │   ├── AttendanceDTO.java
    │   │   ├── MarksDTO.java
    │   │   ├── NoticeDTO.java
    │   │   ├── DashboardDTO.java
    │   │   └── AIPredictionDTO.java
    │   ├── entity/
    │   │   ├── User.java
    │   │   ├── Student.java
    │   │   ├── Faculty.java
    │   │   ├── Attendance.java
    │   │   ├── Marks.java
    │   │   └── Notice.java
    │   ├── exception/
    │   │   ├── ResourceNotFoundException.java
    │   │   ├── DuplicateResourceException.java
    │   │   ├── InvalidOperationException.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── StudentRepository.java
    │   │   ├── FacultyRepository.java
    │   │   ├── AttendanceRepository.java
    │   │   ├── MarksRepository.java
    │   │   └── NoticeRepository.java
    │   ├── security/
    │   │   └── CustomUserDetailsService.java
    │   └── service/
    │       ├── StudentService.java
    │       ├── FacultyService.java
    │       ├── AttendanceService.java
    │       ├── MarksService.java
    │       ├── NoticeService.java
    │       └── impl/
    │           ├── StudentServiceImpl.java
    │           ├── FacultyServiceImpl.java
    │           ├── AttendanceServiceImpl.java
    │           ├── MarksServiceImpl.java
    │           ├── NoticeServiceImpl.java
    │           ├── DashboardService.java
    │           └── AIService.java
    └── resources/
        ├── application.properties
        ├── static/
        │   ├── css/style.css
        │   └── js/{main.js, attendance.js}
        └── templates/
            ├── common/{layout.html, login.html}
            ├── error/{error.html, 403.html}
            ├── admin/ (dashboard, students, faculty, notices, marks, ai-insights, ...)
            ├── faculty/ (dashboard, attendance, marks, profile, ...)
            └── student/ (dashboard, attendance, marks, result, notices, ai-insights, profile)
```

---

## 🚀 Getting Started

### Prerequisites
- **JDK 23** installed and configured
- **Maven 3.9+**
- **MySQL 8.0+** running locally on port 3306

### 1. Configure Database
Ensure MySQL is running. The application will auto-create the `college_erp` database
(`createDatabaseIfNotExist=true` is set), so you don't need to create it manually.

Default credentials (in `application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/college_erp?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```
Update `username`/`password` if your MySQL setup differs.

### 2. Run with IntelliJ IDEA
1. Open the project folder (`college-erp`) in IntelliJ IDEA.
2. Wait for Maven to download dependencies.
3. Ensure **Project SDK** is set to **Java 23** (File → Project Structure → SDK).
4. Run `CollegeErpApplication.java` (right-click → Run).

### 3. Run with Maven CLI
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access the Application
Open your browser at: **http://localhost:8080**

On first run, `DataInitializer` automatically seeds the database with sample
admin, faculty, and student accounts plus realistic attendance and marks data.

---

## 🔑 Demo Login Credentials

| Role    | Username    | Password    |
|---------|-------------|-------------|
| Admin   | `admin`     | `admin123`  |
| Faculty | `fac001`    | `faculty123`|
| Student | `cs2021001` | `student123`|

> All seeded faculty usernames follow the pattern `fac001`–`fac005`.
> All seeded student usernames are their enrollment numbers in lowercase
> (e.g. `cs2021001`, `ec2021001`, `me2021001`, etc.) — password `student123` for all.

---

## 🧠 AI Algorithm Details

The `AIService` class implements rule-based statistical analysis (no external AI APIs):

- **Attendance Risk**: classified as `LOW` (≥80%), `MEDIUM` (≥75%), `HIGH` (≥60%), `CRITICAL` (<60%), also factoring in recent absences (last 30 days).
- **Performance Score**: `(attendance × 0.4) + (averageMarks × 0.6)`.
- **Predicted Grade**: `(averageMarks × 0.7) + (attendance × 0.3)` mapped to O/A+/A/B+/B/C/F.
- **Trend Analysis**: compares last-30-day vs previous-30-day attendance, and last two semesters' marks averages.
- **Overall Risk**: weighted scoring across attendance, marks, and recent absences → `LOW`/`MEDIUM`/`HIGH`.
- **Suggestions Engine**: generates contextual recommendations (e.g., "attend N consecutive classes to reach 75%").

---

## 📊 Database Schema (Auto-Generated)

| Table | Key Relationships |
|---|---|
| `users` | Base auth table for ADMIN/FACULTY/STUDENT |
| `students` | OneToOne `users`, OneToMany `attendance`, OneToMany `marks` |
| `faculty` | OneToOne `users`, OneToMany `attendance` |
| `attendance` | ManyToOne `students`, ManyToOne `faculty` |
| `marks` | ManyToOne `students` |
| `notices` | Standalone, filtered by audience |

`spring.jpa.hibernate.ddl-auto=update` automatically creates/updates tables on startup.

---

## 🔒 Security Configuration

- Passwords hashed with **BCrypt (strength 12)**
- Role-based URL authorization:
  - `/admin/**` → `ROLE_ADMIN`
  - `/faculty/**` → `ROLE_ADMIN`, `ROLE_FACULTY`
  - `/student/**` → `ROLE_ADMIN`, `ROLE_STUDENT`
- Custom login page at `/login`
- Single-session enforcement per user
- Custom 403 / error pages

---

## 📝 License

This project is provided as a sample/reference implementation for educational purposes.
