package com.college.erp.service.impl;

import com.college.erp.dto.StudentDTO;
import com.college.erp.entity.Student;
import com.college.erp.entity.User;
import com.college.erp.exception.DuplicateResourceException;
import com.college.erp.exception.ResourceNotFoundException;
import com.college.erp.repository.AttendanceRepository;
import com.college.erp.repository.MarksRepository;
import com.college.erp.repository.StudentRepository;
import com.college.erp.repository.UserRepository;
import com.college.erp.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StudentDTO createStudent(StudentDTO dto) {
        if (studentRepository.existsByEnrollmentNo(dto.getEnrollmentNo())) {
            throw new DuplicateResourceException("Student with enrollment no " + dto.getEnrollmentNo() + " already exists");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Student with email " + dto.getEmail() + " already exists");
        }

        String username = dto.getUsername() != null ? dto.getUsername()
                : dto.getEnrollmentNo().toLowerCase();
        String password = dto.getPassword() != null ? dto.getPassword() : "student123";

        User user = User.builder()
                .username(username)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(password))
                .role(User.Role.ROLE_STUDENT)
                .fullName(dto.getFirstName() + " " + dto.getLastName())
                .phone(dto.getPhone())
                .enabled(true)
                .build();
        userRepository.save(user);

        Student student = buildStudentFromDTO(dto);
        student.setUser(user);
        Student saved = studentRepository.save(student);
        log.info("Student created: {}", saved.getEnrollmentNo());
        return StudentDTO.fromEntity(saved);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = findStudentEntityById(id);

        // Check duplicates only if changed
        if (!student.getEmail().equals(dto.getEmail()) &&
                studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email " + dto.getEmail() + " already in use");
        }

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setDepartment(dto.getDepartment());
        student.setSemester(dto.getSemester());
        student.setAcademicYear(dto.getAcademicYear());
        student.setAddress(dto.getAddress());
        student.setGuardianName(dto.getGuardianName());
        student.setGuardianPhone(dto.getGuardianPhone());
        student.setActive(dto.isActive());
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            student.setGender(Student.Gender.valueOf(dto.getGender()));
        }

        // Update user email if changed
        if (student.getUser() != null) {
            student.getUser().setEmail(dto.getEmail());
            student.getUser().setFullName(dto.getFirstName() + " " + dto.getLastName());
        }

        Student updated = studentRepository.save(student);
        log.info("Student updated: {}", updated.getEnrollmentNo());
        return StudentDTO.fromEntity(updated);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = findStudentEntityById(id);
        student.setActive(false);
        if (student.getUser() != null) {
            student.getUser().setEnabled(false);
        }
        studentRepository.save(student);
        log.info("Student soft-deleted: {}", student.getEnrollmentNo());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        Student student = findStudentEntityById(id);
        StudentDTO dto = StudentDTO.fromEntity(student);
        enrichWithStats(dto, student.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByEnrollmentNo(String enrollmentNo) {
        Student student = studentRepository.findByEnrollmentNo(enrollmentNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + enrollmentNo));
        return StudentDTO.fromEntity(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
        return StudentDTO.fromEntity(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(StudentDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> searchStudents(String query, Pageable pageable) {
        return studentRepository.searchStudents(query, pageable).map(StudentDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department).stream()
                .map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsBySemester(Integer semester) {
        return studentRepository.findBySemester(semester).stream()
                .map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByDepartmentAndSemester(String dept, Integer semester) {
        return studentRepository.findByDepartmentAndSemester(dept, semester).stream()
                .map(StudentDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return studentRepository.findAllDepartments();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalStudentCount() {
        return studentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveStudentCount() {
        return studentRepository.countByActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for: " + username));
        StudentDTO dto = StudentDTO.fromEntity(student);
        enrichWithStats(dto, student.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEnrollmentNo(String enrollmentNo) {
        return studentRepository.existsByEnrollmentNo(enrollmentNo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Student findStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private Student buildStudentFromDTO(StudentDTO dto) {
        Student s = new Student();
        s.setEnrollmentNo(dto.getEnrollmentNo());
        s.setFirstName(dto.getFirstName());
        s.setLastName(dto.getLastName());
        s.setEmail(dto.getEmail());
        s.setPhone(dto.getPhone());
        s.setDateOfBirth(dto.getDateOfBirth());
        s.setDepartment(dto.getDepartment());
        s.setSemester(dto.getSemester());
        s.setAcademicYear(dto.getAcademicYear());
        s.setAddress(dto.getAddress());
        s.setGuardianName(dto.getGuardianName());
        s.setGuardianPhone(dto.getGuardianPhone());
        s.setAdmissionDate(dto.getAdmissionDate());
        s.setActive(true);
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            s.setGender(Student.Gender.valueOf(dto.getGender()));
        }
        return s;
    }

    private void enrichWithStats(StudentDTO dto, Long studentId) {
        long total = attendanceRepository.countTotalByStudent(studentId);
        long present = attendanceRepository.countPresentByStudent(studentId);
        dto.setAttendancePercentage(total > 0 ? (present * 100.0 / total) : 0.0);

        Double avg = marksRepository.getAveragePercentageByStudent(studentId);
        dto.setAverageMarks(avg != null ? avg : 0.0);

        double att = dto.getAttendancePercentage();
        double marks = dto.getAverageMarks();
        if (att >= 85 && marks >= 75) dto.setPerformanceCategory("EXCELLENT");
        else if (att >= 75 && marks >= 60) dto.setPerformanceCategory("GOOD");
        else if (att >= 65 && marks >= 50) dto.setPerformanceCategory("AVERAGE");
        else if (att >= 50 || marks >= 40)  dto.setPerformanceCategory("BELOW_AVERAGE");
        else dto.setPerformanceCategory("POOR");
    }
}
