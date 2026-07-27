package com.college.erp.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIPredictionDTO {

    private Long studentId;
    private String studentName;
    private String enrollmentNo;
    private String department;
    private Integer semester;

    // Attendance metrics
    private double attendancePercentage;
    private String attendanceRisk;        // LOW, MEDIUM, HIGH, CRITICAL
    private int consecutiveAbsences;

    // Performance metrics
    private double averageMarks;
    private double performanceScore;      // 0-100 computed score
    private String performanceCategory;  // EXCELLENT, GOOD, AVERAGE, BELOW_AVERAGE, POOR
    private String predictedGrade;        // Predicted final grade

    // Trend analysis
    private String attendanceTrend;       // IMPROVING, STABLE, DECLINING
    private String performanceTrend;

    // AI suggestions
    private List<String> suggestions;
    private List<String> warnings;
    private String overallRisk;           // LOW, MEDIUM, HIGH

    // Detailed breakdown
    private List<SubjectPerformance> subjectPerformances;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectPerformance {
        private String subject;
        private double marksPercentage;
        private double attendancePercentage;
        private String grade;
        private String status;           // PASS, FAIL, AT_RISK
    }
}
