package com.college.erp.dto;

import com.college.erp.entity.Faculty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDTO {

    private Long id;

    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^[A-Z0-9]{4,20}$", message = "Invalid employee ID format")
    private String employeeId;

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

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Designation is required")
    private String designation;

    private String specialization;
    private LocalDate dateOfJoining;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String qualification;
    private Integer experienceYears;
    private boolean active;

    // For user creation
    private String username;
    private String password;

    // Computed
    private Integer totalStudentsHandled;
    private Long totalAttendanceMarked;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static FacultyDTO fromEntity(Faculty faculty) {
        return FacultyDTO.builder()
                .id(faculty.getId())
                .employeeId(faculty.getEmployeeId())
                .firstName(faculty.getFirstName())
                .lastName(faculty.getLastName())
                .email(faculty.getEmail())
                .phone(faculty.getPhone())
                .department(faculty.getDepartment())
                .designation(faculty.getDesignation())
                .specialization(faculty.getSpecialization())
                .dateOfJoining(faculty.getDateOfJoining())
                .dateOfBirth(faculty.getDateOfBirth())
                .gender(faculty.getGender() != null ? faculty.getGender().name() : null)
                .address(faculty.getAddress())
                .qualification(faculty.getQualification())
                .experienceYears(faculty.getExperienceYears())
                .active(faculty.isActive())
                .build();
    }
}
