package com.college.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "marks", indexes = {
        @Index(name = "idx_marks_student", columnList = "student_id"),
        @Index(name = "idx_marks_semester", columnList = "semester"),
        @Index(name = "idx_marks_subject", columnList = "subject")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String subject;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("100.0")
    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained;

    @NotNull
    @DecimalMin("1.0") @DecimalMax("100.0")
    @Column(name = "max_marks", nullable = false)
    private Double maxMarks;

    @Column(name = "grade", length = 5)
    private String grade;

    @NotNull
    @Min(1) @Max(8)
    @Column(nullable = false)
    private Integer semester;

    @NotBlank
    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(length = 100)
    private String department;

    @Column(length = 200)
    private String remarks;

    @Column(name = "is_pass")
    private Boolean pass;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateGrade() {
        if (marksObtained != null && maxMarks != null) {
            double percentage = (marksObtained / maxMarks) * 100;
            this.grade = computeGrade(percentage);
            this.pass = percentage >= 40.0;
        }
    }

    public static String computeGrade(double percentage) {
        if (percentage >= 90) return "O";
        else if (percentage >= 80) return "A+";
        else if (percentage >= 70) return "A";
        else if (percentage >= 60) return "B+";
        else if (percentage >= 50) return "B";
        else if (percentage >= 40) return "C";
        else return "F";
    }

    public double getPercentage() {
        if (maxMarks == null || maxMarks == 0) return 0;
        return (marksObtained / maxMarks) * 100;
    }
}
