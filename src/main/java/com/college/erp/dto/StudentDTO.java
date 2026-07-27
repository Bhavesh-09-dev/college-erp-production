package com.college.erp.dto;

import com.college.erp.entity.Student;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {

    private Long id;

    @NotBlank(message = "Enrollment number is required")
    @Pattern(regexp = "^[A-Z0-9]{6,20}$", message = "Invalid enrollment number format")
    private String enrollmentNo;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester cannot exceed 8")
    private Integer semester;

    @NotNull(message = "Academic year is required")
    @Min(value = 1, message = "Academic year must be at least 1")
    @Max(value = 4, message = "Academic year cannot exceed 4")
    private Integer academicYear;

    private String address;
    private String guardianName;
    private String guardianPhone;
    private String gender;
    private LocalDate admissionDate;
    private boolean active;

    // For user creation
    private String username;
    private String password;

    // Computed fields
    private Double attendancePercentage;
    private Double averageMarks;
    private String performanceCategory;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static StudentDTO fromEntity(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .enrollmentNo(student.getEnrollmentNo())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .department(student.getDepartment())
                .semester(student.getSemester())
                .academicYear(student.getAcademicYear())
                .address(student.getAddress())
                .guardianName(student.getGuardianName())
                .guardianPhone(student.getGuardianPhone())
                .gender(student.getGender() != null ? student.getGender().name() : null)
                .admissionDate(student.getAdmissionDate())
                .active(student.isActive())
                .build();
    }
}
