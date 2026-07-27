package com.college.erp.dto;

import com.college.erp.entity.Attendance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;
    private String enrollmentNo;

    private Long facultyId;
    private String facultyName;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Status is required")
    private Attendance.Status status;

    @NotBlank(message = "Subject is required")
    private String subject;

    private String semester;
    private String department;
    private String remarks;

    // Computed fields for reports
    private Long totalClasses;
    private Long presentCount;
    private Long absentCount;
    private Double attendancePercentage;
    private boolean atRisk;

    public static AttendanceDTO fromEntity(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStudentName(attendance.getStudent().getFullName());
        dto.setEnrollmentNo(attendance.getStudent().getEnrollmentNo());
        if (attendance.getFaculty() != null) {
            dto.setFacultyId(attendance.getFaculty().getId());
            dto.setFacultyName(attendance.getFaculty().getFullName());
        }
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setStatus(attendance.getStatus());
        dto.setSubject(attendance.getSubject());
        dto.setSemester(attendance.getSemester());
        dto.setDepartment(attendance.getDepartment());
        dto.setRemarks(attendance.getRemarks());
        return dto;
    }
}
