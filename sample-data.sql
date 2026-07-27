-- ============================================================
-- Smart College ERP Portal - Sample Data Reference Script
-- ============================================================
-- NOTE: This file is for REFERENCE ONLY.
-- The application automatically seeds this data on first run
-- via com.college.erp.config.DataInitializer (CommandLineRunner).
--
-- If you need to manually reset the database, run:
--   DROP DATABASE IF EXISTS college_erp;
-- Then restart the Spring Boot application - tables and seed
-- data will be created automatically (ddl-auto=update +
-- DataInitializer).
-- ============================================================

CREATE DATABASE IF NOT EXISTS college_erp;
USE college_erp;

-- ============================================================
-- Table: users  (created automatically by Hibernate)
-- ============================================================
-- Columns: id, username, email, password (BCrypt hashed),
--          role (ROLE_ADMIN/ROLE_FACULTY/ROLE_STUDENT),
--          enabled, full_name, phone, created_at, updated_at

-- Sample admin (password: admin123, BCrypt-encoded at runtime)
-- username: admin | role: ROLE_ADMIN

-- ============================================================
-- Table: faculty (created automatically by Hibernate)
-- ============================================================
-- Sample faculty members (password for all: faculty123)
-- | employee_id | name              | department         | designation          |
-- |-------------|-------------------|--------------------|----------------------|
-- | FAC001      | Dr. Rajesh Kumar  | Computer Science    | Professor            |
-- | FAC002      | Prof. Priya Sharma| Computer Science    | Associate Professor  |
-- | FAC003      | Dr. Amit Patel    | Mathematics         | Professor            |
-- | FAC004      | Dr. Sunita Verma  | Electronics         | HOD                  |
-- | FAC005      | Prof. Vikram Singh| Mechanical          | Associate Professor  |
--
-- Login usernames: fac001, fac002, fac003, fac004, fac005

-- ============================================================
-- Table: students (created automatically by Hibernate)
-- ============================================================
-- Sample students (password for all: student123)
-- | enrollment_no | name              | department       | semester |
-- |---------------|-------------------|------------------|----------|
-- | CS2021001     | Aarav Mehta       | Computer Science | 1        |
-- | CS2021002     | Ananya Joshi      | Computer Science | 1        |
-- | CS2021003     | Rohan Gupta       | Computer Science | 1        |
-- | CS2021004     | Priya Nair        | Computer Science | 2        |
-- | CS2021005     | Karan Shah        | Computer Science | 2        |
-- | CS2021006     | Sneha Reddy       | Computer Science | 2        |
-- | EC2021001     | Arjun Yadav       | Electronics      | 1        |
-- | EC2021002     | Kavya Pillai      | Electronics      | 1        |
-- | ME2021001     | Rahul Tiwari      | Mechanical       | 1        |
-- | ME2021002     | Divya Bose        | Mechanical       | 1        |
-- | CS2022001     | Siddharth Malhotra| Computer Science | 1        |
-- | CS2022002     | Nisha Agarwal     | Computer Science | 1        |
-- | MA2021001     | Tanvi Iyer        | Mathematics      | 1        |
-- | MA2021002     | Vivek Choudhary   | Mathematics      | 1        |
-- | CS2021007     | Pooja Bansal      | Computer Science | 3        |
--
-- Login usernames: cs2021001, cs2021002, ... (lowercase enrollment no)

-- ============================================================
-- Table: attendance (created automatically by Hibernate)
-- ============================================================
-- Each student has ~40 attendance records per subject
-- (5 subjects per department) with varied present/absent
-- patterns to demonstrate AI risk detection:
--   - CS2021005: ~67% attendance (HIGH risk)
--   - ME2021001: ~50% attendance (CRITICAL risk)
--   - EC2021001: ~75% attendance (borderline/MEDIUM risk)
--   - Most others: ~80-90% attendance (LOW risk)

-- ============================================================
-- Table: marks (created automatically by Hibernate)
-- ============================================================
-- Each student has MID_TERM and END_TERM marks for each
-- subject in their current semester, with grades auto-computed:
--   O (>=90%), A+ (>=80%), A (>=70%), B+ (>=60%),
--   B (>=50%), C (>=40%), F (<40%)

-- ============================================================
-- Table: notices (created automatically by Hibernate)
-- ============================================================
-- 5 sample notices covering exam schedules, sports day,
-- faculty development programs, library timings, and
-- scholarship deadlines with varied priorities and audiences.

-- ============================================================
-- LOGIN CREDENTIALS SUMMARY
-- ============================================================
-- ADMIN:    admin / admin123
-- FACULTY:  fac001 / faculty123  (and fac002-fac005)
-- STUDENT:  cs2021001 / student123  (and all other enrollment numbers, lowercase)
-- ============================================================
