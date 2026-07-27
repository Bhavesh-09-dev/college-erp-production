package com.college.erp.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    // Counts
    private long totalStudents;
    private long totalFaculty;
    private long totalNotices;
    private long activeStudents;
    private long activeFaculty;

    // Attendance stats
    private double overallAttendancePercentage;
    private long studentsAtRisk;          // below 75%
    private long studentsInDanger;        // below 60%
    private Map<String, Long> attendanceByDepartment;

    // Marks stats
    private double overallAverageMarks;
    private Map<String, Long> gradeDistribution;
    private Map<String, Double> avgMarksByDepartment;
    private Map<String, Double> avgMarksBySubject;

    // Department-wise student count
    private Map<String, Long> studentsByDepartment;
    private Map<String, Long> studentsBySemester;

    // Recent data
    private List<StudentDTO> recentStudents;
    private List<NoticeDTO> recentNotices;
    private List<StudentDTO> atRiskStudents;

    // Monthly attendance trend (for chart)
    private List<String> monthLabels;
    private List<Double> monthlyAttendance;

    // Semester-wise performance (for chart)
    private List<String> semesterLabels;
    private List<Double> semesterAvgMarks;
}
