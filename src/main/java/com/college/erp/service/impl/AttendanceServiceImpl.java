package com.college.erp.service.impl;

import com.college.erp.dto.AttendanceDTO;
import com.college.erp.entity.Attendance;
import com.college.erp.entity.Faculty;
import com.college.erp.entity.Student;
import com.college.erp.exception.ResourceNotFoundException;
import com.college.erp.repository.AttendanceRepository;
import com.college.erp.repository.FacultyRepository;
import com.college.erp.repository.StudentRepository;
import com.college.erp.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public AttendanceDTO markAttendance(AttendanceDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));

        Attendance attendance = Attendance.builder()
                .student(student)
                .attendanceDate(dto.getAttendanceDate() != null ? dto.getAttendanceDate() : LocalDate.now())
                .status(dto.getStatus())
                .subject(dto.getSubject())
                .semester(dto.getSemester())
                .department(dto.getDepartment() != null ? dto.getDepartment() : student.getDepartment())
                .remarks(dto.getRemarks())
                .build();

        if (dto.getFacultyId() != null) {
            facultyRepository.findById(dto.getFacultyId()).ifPresent(attendance::setFaculty);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceDTO.fromEntity(saved);
    }

    @Override
    public List<AttendanceDTO> markBulkAttendance(List<AttendanceDTO> dtos) {
        return dtos.stream().map(this::markAttendance).collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));

        attendance.setStatus(dto.getStatus());
        attendance.setRemarks(dto.getRemarks());
        if (dto.getAttendanceDate() != null) attendance.setAttendanceDate(dto.getAttendanceDate());
        if (dto.getSubject() != null) attendance.setSubject(dto.getSubject());

        return AttendanceDTO.fromEntity(attendanceRepository.save(attendance));
    }

    @Override
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        return AttendanceDTO.fromEntity(
                attendanceRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(AttendanceDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByStudentAndSubject(Long studentId, String subject) {
        return attendanceRepository.findByStudentIdAndSubject(studentId, subject).stream()
                .map(AttendanceDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date).stream()
                .map(AttendanceDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByDateRange(LocalDate start, LocalDate end) {
        return attendanceRepository.findByAttendanceDateBetween(start, end).stream()
                .map(AttendanceDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public double getAttendancePercentage(Long studentId) {
        long total = attendanceRepository.countTotalByStudent(studentId);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentByStudent(studentId);
        return Math.round((present * 100.0 / total) * 100.0) / 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getAttendancePercentageBySubject(Long studentId, String subject) {
        long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subject);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subject);
        return Math.round((present * 100.0 / total) * 100.0) / 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getAttendanceBySubjectForStudent(Long studentId) {
        List<Object[]> stats = attendanceRepository.getAttendanceStatsBySubject(studentId);
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : stats) {
            String subject = (String) row[0];
            long total = ((Number) row[1]).longValue();
            long present = ((Number) row[2]).longValue();
            double pct = total > 0 ? Math.round((present * 100.0 / total) * 100.0) / 100.0 : 0.0;
            result.put(subject, pct);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getStudentsAtRisk() {
        List<Object[]> summaries = attendanceRepository.getAllStudentAttendanceSummary();
        List<AttendanceDTO> atRisk = new ArrayList<>();

        for (Object[] row : summaries) {
            Long studentId = ((Number) row[0]).longValue();
            long total = ((Number) row[1]).longValue();
            long present = ((Number) row[2]).longValue();
            double pct = total > 0 ? (present * 100.0 / total) : 0.0;

            if (pct < 75.0) {
                studentRepository.findById(studentId).ifPresent(student -> {
                    AttendanceDTO dto = new AttendanceDTO();
                    dto.setStudentId(studentId);
                    dto.setStudentName(student.getFullName());
                    dto.setEnrollmentNo(student.getEnrollmentNo());
                    dto.setTotalClasses(total);
                    dto.setPresentCount(present);
                    dto.setAbsentCount(total - present);
                    dto.setAttendancePercentage(Math.round(pct * 100.0) / 100.0);
                    dto.setAtRisk(true);
                    atRisk.add(dto);
                });
            }
        }
        return atRisk;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllSubjects() {
        return attendanceRepository.findAllSubjects();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceSummaryForStudent(Long studentId) {
        long total = attendanceRepository.countTotalByStudent(studentId);
        long present = attendanceRepository.countPresentByStudent(studentId);
        double pct = total > 0 ? (present * 100.0 / total) : 0.0;

        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(studentId);
        dto.setTotalClasses(total);
        dto.setPresentCount(present);
        dto.setAbsentCount(total - present);
        dto.setAttendancePercentage(Math.round(pct * 100.0) / 100.0);
        dto.setAtRisk(pct < 75.0);
        return dto;
    }
}
