package com.college.erp.config;

import com.college.erp.entity.*;
import com.college.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final NoticeRepository noticeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, skipping seed data.");
            return;
        }
        log.info("Initializing database with sample data...");
        createAdmin();
        createFaculty();
        createStudents();
        createNotices();
        log.info("✅ Database initialization complete!");
    }

    private void createAdmin() {
        User admin = User.builder()
                .username("admin")
                .email("admin@college.edu")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ROLE_ADMIN)
                .fullName("System Administrator")
                .phone("9876543210")
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("Admin user created: admin / admin123");
    }

    private void createFaculty() {
        String[][] facultyData = {
            {"FAC001","Dr. Rajesh","Kumar","rajesh.kumar@college.edu","9876501001","Computer Science","Professor","Java, OOP"},
            {"FAC002","Prof. Priya","Sharma","priya.sharma@college.edu","9876501002","Computer Science","Associate Professor","Data Structures"},
            {"FAC003","Dr. Amit","Patel","amit.patel@college.edu","9876501003","Mathematics","Professor","Calculus, Linear Algebra"},
            {"FAC004","Dr. Sunita","Verma","sunita.verma@college.edu","9876501004","Electronics","HOD","Circuit Design"},
            {"FAC005","Prof. Vikram","Singh","vikram.singh@college.edu","9876501005","Mechanical","Associate Professor","Thermodynamics"}
        };

        for (String[] f : facultyData) {
            User user = User.builder()
                    .username(f[0].toLowerCase())
                    .email(f[3])
                    .password(passwordEncoder.encode("faculty123"))
                    .role(User.Role.ROLE_FACULTY)
                    .fullName(f[1] + " " + f[2])
                    .enabled(true)
                    .build();
            userRepository.save(user);

            Faculty faculty = Faculty.builder()
                    .employeeId(f[0])
                    .firstName(f[1])
                    .lastName(f[2])
                    .email(f[3])
                    .phone(f[4])
                    .department(f[5])
                    .designation(f[6])
                    .specialization(f[7])
                    .dateOfJoining(LocalDate.of(2018, 7, 1))
                    .gender(Faculty.Gender.MALE)
                    .qualification("Ph.D")
                    .experienceYears(8)
                    .active(true)
                    .user(user)
                    .build();
            facultyRepository.save(faculty);
        }
        log.info("5 faculty members created");
    }

    private void createStudents() {
        String[][] studentData = {
            {"CS2021001","Aarav","Mehta","aarav.mehta@student.edu","9900001001","Computer Science","1","3"},
            {"CS2021002","Ananya","Joshi","ananya.joshi@student.edu","9900001002","Computer Science","1","3"},
            {"CS2021003","Rohan","Gupta","rohan.gupta@student.edu","9900001003","Computer Science","1","3"},
            {"CS2021004","Priya","Nair","priya.nair@student.edu","9900001004","Computer Science","2","3"},
            {"CS2021005","Karan","Shah","karan.shah@student.edu","9900001005","Computer Science","2","3"},
            {"CS2021006","Sneha","Reddy","sneha.reddy@student.edu","9900001006","Computer Science","2","3"},
            {"EC2021001","Arjun","Yadav","arjun.yadav@student.edu","9900002001","Electronics","1","3"},
            {"EC2021002","Kavya","Pillai","kavya.pillai@student.edu","9900002002","Electronics","1","3"},
            {"ME2021001","Rahul","Tiwari","rahul.tiwari@student.edu","9900003001","Mechanical","1","3"},
            {"ME2021002","Divya","Bose","divya.bose@student.edu","9900003002","Mechanical","1","3"},
            {"CS2022001","Siddharth","Malhotra","siddharth.m@student.edu","9900004001","Computer Science","1","2"},
            {"CS2022002","Nisha","Agarwal","nisha.agarwal@student.edu","9900004002","Computer Science","1","2"},
            {"MA2021001","Tanvi","Iyer","tanvi.iyer@student.edu","9900005001","Mathematics","1","3"},
            {"MA2021002","Vivek","Choudhary","vivek.choudhary@student.edu","9900005002","Mathematics","1","3"},
            {"CS2021007","Pooja","Bansal","pooja.bansal@student.edu","9900001007","Computer Science","3","3"}
        };

        List<Faculty> faculties = facultyRepository.findAll();
        Faculty csHead = faculties.isEmpty() ? null : faculties.get(0);

        for (int i = 0; i < studentData.length; i++) {
            String[] s = studentData[i];
            User user = User.builder()
                    .username(s[0].toLowerCase())
                    .email(s[3])
                    .password(passwordEncoder.encode("student123"))
                    .role(User.Role.ROLE_STUDENT)
                    .fullName(s[1] + " " + s[2])
                    .enabled(true)
                    .build();
            userRepository.save(user);

            Student student = Student.builder()
                    .enrollmentNo(s[0])
                    .firstName(s[1])
                    .lastName(s[2])
                    .email(s[3])
                    .phone(s[4])
                    .department(s[5])
                    .semester(Integer.parseInt(s[6]))
                    .academicYear(Integer.parseInt(s[7]))
                    .dateOfBirth(LocalDate.of(2001, (i % 12) + 1, (i % 28) + 1))
                    .admissionDate(LocalDate.of(2021, 7, 15))
                    .gender(i % 2 == 0 ? Student.Gender.MALE : Student.Gender.FEMALE)
                    .guardianName("Parent of " + s[1])
                    .guardianPhone("98000" + String.format("%05d", i))
                    .active(true)
                    .user(user)
                    .build();
            Student saved = studentRepository.save(student);

            // Create attendance records
            createAttendanceForStudent(saved, csHead);

            // Create marks records
            createMarksForStudent(saved);
        }
        log.info("15 students created with attendance and marks");
    }

    private void createAttendanceForStudent(Student student, Faculty faculty) {
        String[] subjects = getSubjectsForDepartment(student.getDepartment());
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        for (String subject : subjects) {
            // Create 40 attendance records per subject
            for (int day = 0; day < 40; day++) {
                LocalDate date = startDate.plusDays(day * 2L);
                // Vary attendance: some students have low attendance
                boolean isPresent = shouldBePresent(student.getEnrollmentNo(), day);

                Attendance attendance = Attendance.builder()
                        .student(student)
                        .faculty(faculty)
                        .attendanceDate(date)
                        .status(isPresent ? Attendance.Status.PRESENT : Attendance.Status.ABSENT)
                        .subject(subject)
                        .semester(String.valueOf(student.getSemester()))
                        .department(student.getDepartment())
                        .build();
                attendanceRepository.save(attendance);
            }
        }
    }

    private boolean shouldBePresent(String enrollmentNo, int day) {
        // Create varied attendance patterns
        return switch (enrollmentNo) {
            case "CS2021003" -> day % 5 != 0;         // ~80% attendance
            case "CS2021005" -> day % 3 != 0;         // ~67% attendance (at risk)
            case "EC2021001" -> day % 4 != 0;         // ~75% attendance (borderline)
            case "ME2021001" -> day % 2 != 0;         // ~50% attendance (critical)
            default -> day % 7 != 0;                  // ~86% good attendance
        };
    }

    private void createMarksForStudent(Student student) {
        String[] subjects = getSubjectsForDepartment(student.getDepartment());
        String[] examTypes = {"MID_TERM", "END_TERM"};
        int[] semesters = {student.getSemester()};

        for (int sem : semesters) {
            for (String subject : subjects) {
                for (String examType : examTypes) {
                    double baseMarks = getBaseMarksForStudent(student.getEnrollmentNo());
                    double variation = (Math.random() - 0.5) * 20;
                    double marks = Math.max(20, Math.min(100, baseMarks + variation));

                    Marks m = Marks.builder()
                            .student(student)
                            .subject(subject)
                            .marksObtained(Math.round(marks * 10.0) / 10.0)
                            .maxMarks(100.0)
                            .semester(sem)
                            .examType(examType)
                            .academicYear("2024-25")
                            .department(student.getDepartment())
                            .build();
                    m.calculateGrade();
                    marksRepository.save(m);
                }
            }
        }
    }

    private double getBaseMarksForStudent(String enrollmentNo) {
        return switch (enrollmentNo) {
            case "CS2021001" -> 88;
            case "CS2021002" -> 82;
            case "CS2021003" -> 76;
            case "CS2021004" -> 91;
            case "CS2021005" -> 55;
            case "CS2021006" -> 70;
            case "EC2021001" -> 65;
            case "EC2021002" -> 78;
            case "ME2021001" -> 45;
            case "ME2021002" -> 83;
            default          -> 72;
        };
    }

    private String[] getSubjectsForDepartment(String dept) {
        return switch (dept) {
            case "Computer Science" -> new String[]{"Data Structures","Algorithms","DBMS","OS","Computer Networks"};
            case "Electronics"       -> new String[]{"Circuit Theory","Electronics Devices","Digital Electronics","Signals","Control Systems"};
            case "Mechanical"        -> new String[]{"Thermodynamics","Fluid Mechanics","Machine Design","Manufacturing","Strength of Materials"};
            case "Mathematics"       -> new String[]{"Calculus","Linear Algebra","Probability","Complex Analysis","Numerical Methods"};
            default                  -> new String[]{"Subject 1","Subject 2","Subject 3","Subject 4","Subject 5"};
        };
    }

    private void createNotices() {
        Notice[] notices = {
            Notice.builder()
                .title("Semester Examination Schedule 2024-25")
                .content("The semester examinations for all departments will commence from December 1, 2024. " +
                         "Students are advised to collect their hall tickets from the examination cell. " +
                         "No student will be allowed to appear in the exam without a valid hall ticket.")
                .priority(Notice.Priority.URGENT)
                .targetAudience(Notice.TargetAudience.ALL)
                .postedBy("Examination Controller")
                .expiryDate(LocalDate.of(2024, 12, 31))
                .active(true)
                .build(),
            Notice.builder()
                .title("Annual Sports Day - Registration Open")
                .content("Annual Sports Day will be held on November 15, 2024. Students interested in " +
                         "participating in various sports events are requested to register with their " +
                         "respective department sports coordinators by November 10, 2024.")
                .priority(Notice.Priority.NORMAL)
                .targetAudience(Notice.TargetAudience.STUDENTS)
                .postedBy("Sports Department")
                .expiryDate(LocalDate.of(2024, 11, 15))
                .active(true)
                .build(),
            Notice.builder()
                .title("Faculty Development Program")
                .content("A 3-day Faculty Development Program on Modern Teaching Methodologies will be " +
                         "conducted from October 20-22, 2024. All faculty members are requested to attend.")
                .priority(Notice.Priority.HIGH)
                .targetAudience(Notice.TargetAudience.FACULTY)
                .postedBy("HR Department")
                .active(true)
                .build(),
            Notice.builder()
                .title("Library Timings Extended")
                .content("The college library will now be open from 8:00 AM to 9:00 PM on all weekdays " +
                         "and 9:00 AM to 5:00 PM on Saturdays during examination period.")
                .priority(Notice.Priority.NORMAL)
                .targetAudience(Notice.TargetAudience.ALL)
                .postedBy("Library")
                .active(true)
                .build(),
            Notice.builder()
                .title("Scholarship Application Deadline")
                .content("Students eligible for merit scholarships must submit their applications by " +
                         "October 31, 2024. Required documents: mark sheets, income certificate, " +
                         "Aadhaar card, and bank account details.")
                .priority(Notice.Priority.HIGH)
                .targetAudience(Notice.TargetAudience.STUDENTS)
                .postedBy("Scholarship Committee")
                .expiryDate(LocalDate.of(2024, 10, 31))
                .active(true)
                .build(),
        };

        for (Notice n : notices) {
            noticeRepository.save(n);
        }
        log.info("5 notices created");
    }
}
