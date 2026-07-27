package com.college.erp.dto;

import com.college.erp.entity.Marks;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;
    private String enrollmentNo;
    private String department;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0", message = "Marks cannot be negative")
    @DecimalMax(value = "100.0", message = "Marks cannot exceed 100")
    private Double marksObtained;

    @NotNull(message = "Max marks is required")
    @DecimalMin(value = "1.0", message = "Max marks must be at least 1")
    @DecimalMax(value = "100.0", message = "Max marks cannot exceed 100")
    private Double maxMarks;

    private String grade;

    @NotNull(message = "Semester is required")
    @Min(value = 1) @Max(value = 8)
    private Integer semester;

    @NotBlank(message = "Exam type is required")
    private String examType;

    private String academicYear;
    private String remarks;
    private Boolean pass;
    private Double percentage;

    public static MarksDTO fromEntity(Marks marks) {
        return MarksDTO.builder()
                .id(marks.getId())
                .studentId(marks.getStudent().getId())
                .studentName(marks.getStudent().getFullName())
                .enrollmentNo(marks.getStudent().getEnrollmentNo())
                .department(marks.getStudent().getDepartment())
                .subject(marks.getSubject())
                .marksObtained(marks.getMarksObtained())
                .maxMarks(marks.getMaxMarks())
                .grade(marks.getGrade())
                .semester(marks.getSemester())
                .examType(marks.getExamType())
                .academicYear(marks.getAcademicYear())
                .remarks(marks.getRemarks())
                .pass(marks.getPass())
                .percentage(marks.getPercentage())
                .build();
    }
}
