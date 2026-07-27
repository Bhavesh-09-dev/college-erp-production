package com.college.erp.service.impl;

import com.college.erp.dto.AIPredictionDTO;
import com.college.erp.entity.Student;
import com.college.erp.repository.AttendanceRepository;
import com.college.erp.repository.MarksRepository;
import com.college.erp.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI Service using pure Java algorithms for student analytics and predictions.
 * No external AI APIs used. Implements statistical and rule-based algorithms.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AIService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    // ─── Thresholds ──────────────────────────────────────────────────────────
    private static final double CRITICAL_ATTENDANCE  = 60.0;
    private static final double HIGH_RISK_ATTENDANCE = 75.0;
    private static final double MED_RISK_ATTENDANCE  = 80.0;
    private static final double PASS_THRESHOLD       = 40.0;
    private static final double GOOD_MARKS           = 75.0;
    private static final double EXCELLENT_MARKS      = 85.0;

    public AIPredictionDTO predictForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        AIPredictionDTO dto = new AIPredictionDTO();
        dto.setStudentId(studentId);
        dto.setStudentName(student.getFullName());
        dto.setEnrollmentNo(student.getEnrollmentNo());
        dto.setDepartment(student.getDepartment());
        dto.setSemester(student.getSemester());

        // Compute attendance
        long total = attendanceRepository.countTotalByStudent(studentId);
        long present = attendanceRepository.countPresentByStudent(studentId);
        double attendancePct = total > 0 ? (present * 100.0 / total) : 0.0;
        dto.setAttendancePercentage(Math.round(attendancePct * 100.0) / 100.0);

        // Recent absences (last 30 days)
        long recentAbsences = attendanceRepository.countRecentAbsences(
                studentId, LocalDate.now().minusDays(30));
        dto.setConsecutiveAbsences((int) recentAbsences);

        // Average marks
        Double avgMarks = marksRepository.getAveragePercentageByStudent(studentId);
        double avg = avgMarks != null ? avgMarks : 0.0;
        dto.setAverageMarks(Math.round(avg * 100.0) / 100.0);

        // Attendance risk classification
        dto.setAttendanceRisk(classifyAttendanceRisk(attendancePct, recentAbsences));

        // Performance score (weighted: 40% attendance + 60% marks)
        double perfScore = (attendancePct * 0.4) + (avg * 0.6);
        dto.setPerformanceScore(Math.round(perfScore * 100.0) / 100.0);

        // Performance category
        dto.setPerformanceCategory(classifyPerformance(avg, attendancePct));

        // Predicted grade
        dto.setPredictedGrade(predictGrade(avg, attendancePct));

        // Trends
        dto.setAttendanceTrend(analyzeAttendanceTrend(studentId, attendancePct));
        dto.setPerformanceTrend(analyzePerformanceTrend(studentId));

        // Subject-level breakdown
        dto.setSubjectPerformances(buildSubjectPerformances(studentId));

        // Overall risk
        dto.setOverallRisk(computeOverallRisk(attendancePct, avg, recentAbsences));

        // Generate suggestions & warnings
        dto.setSuggestions(generateSuggestions(dto));
        dto.setWarnings(generateWarnings(dto));

        return dto;
    }

    public List<AIPredictionDTO> getAtRiskStudents() {
        return studentRepository.findByActive(true).stream()
                .map(s -> {
                    try { return predictForStudent(s.getId()); }
                    catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .filter(p -> p.getAttendancePercentage() < HIGH_RISK_ATTENDANCE ||
                             p.getAverageMarks() < PASS_THRESHOLD)
                .sorted(Comparator.comparingDouble(AIPredictionDTO::getAttendancePercentage))
                .collect(Collectors.toList());
    }

    public List<AIPredictionDTO> getAllPredictions() {
        return studentRepository.findByActive(true).stream()
                .map(s -> {
                    try { return predictForStudent(s.getId()); }
                    catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(AIPredictionDTO::getPerformanceScore).reversed())
                .collect(Collectors.toList());
    }

    // ─── Private Algorithms ────────────────────────────────────────────────

    private String classifyAttendanceRisk(double pct, long recentAbsences) {
        if (pct < CRITICAL_ATTENDANCE || recentAbsences > 10) return "CRITICAL";
        if (pct < HIGH_RISK_ATTENDANCE || recentAbsences > 6) return "HIGH";
        if (pct < MED_RISK_ATTENDANCE  || recentAbsences > 3) return "MEDIUM";
        return "LOW";
    }

    private String classifyPerformance(double marks, double attendance) {
        if (marks >= EXCELLENT_MARKS && attendance >= 85) return "EXCELLENT";
        if (marks >= GOOD_MARKS && attendance >= 75) return "GOOD";
        if (marks >= 50 && attendance >= 65) return "AVERAGE";
        if (marks >= PASS_THRESHOLD || attendance >= 50) return "BELOW_AVERAGE";
        return "POOR";
    }

    private String predictGrade(double avg, double attendance) {
        // Weighted prediction: marks have heavier weight
        double predictive = (avg * 0.7) + (attendance * 0.3);

        if (predictive >= 90) return "O (Outstanding)";
        if (predictive >= 80) return "A+ (Excellent)";
        if (predictive >= 70) return "A (Very Good)";
        if (predictive >= 60) return "B+ (Good)";
        if (predictive >= 50) return "B (Average)";
        if (predictive >= 40) return "C (Pass)";
        return "F (Fail)";
    }

    private String analyzeAttendanceTrend(Long studentId, double currentPct) {
        // Compare last 30 days vs previous 30 days
        LocalDate now = LocalDate.now();
        long recentTotal = attendanceRepository
                .findByStudentIdAndAttendanceDateBetween(studentId, now.minusDays(30), now).size();
        long prevTotal = attendanceRepository
                .findByStudentIdAndAttendanceDateBetween(studentId, now.minusDays(60), now.minusDays(31)).size();

        if (recentTotal == 0 && prevTotal == 0) return "INSUFFICIENT_DATA";

        // Count present in each window
        long recentPresent = attendanceRepository
                .findByStudentIdAndAttendanceDateBetween(studentId, now.minusDays(30), now)
                .stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();
        long prevPresent = attendanceRepository
                .findByStudentIdAndAttendanceDateBetween(studentId, now.minusDays(60), now.minusDays(31))
                .stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();

        double recentPct = recentTotal > 0 ? (recentPresent * 100.0 / recentTotal) : 0;
        double prevPct   = prevTotal   > 0 ? (prevPresent   * 100.0 / prevTotal)   : 0;

        double diff = recentPct - prevPct;
        if (diff > 5)  return "IMPROVING";
        if (diff < -5) return "DECLINING";
        return "STABLE";
    }

    private String analyzePerformanceTrend(Long studentId) {
        List<Object[]> semRows = marksRepository.getAverageMarksBySemester();
        if (semRows.size() < 2) return "INSUFFICIENT_DATA";

        // Look at last 2 semesters
        double prevAvg = ((Number) semRows.get(semRows.size() - 2)[1]).doubleValue();
        double currAvg = ((Number) semRows.get(semRows.size() - 1)[1]).doubleValue();

        double diff = currAvg - prevAvg;
        if (diff > 5)  return "IMPROVING";
        if (diff < -5) return "DECLINING";
        return "STABLE";
    }

    private List<AIPredictionDTO.SubjectPerformance> buildSubjectPerformances(Long studentId) {
        List<Object[]> attStats = attendanceRepository.getAttendanceStatsBySubject(studentId);

        // Build subject marks map
        Map<String, Double> subjectMarks = new LinkedHashMap<>();
        marksRepository.findByStudentId(studentId).forEach(m -> {
            subjectMarks.merge(m.getSubject(), m.getPercentage(), (a, b) -> (a + b) / 2.0);
        });

        // Build subject attendance map
        Map<String, Double> subjectAtt = new LinkedHashMap<>();
        for (Object[] row : attStats) {
            String subject = (String) row[0];
            long total = ((Number) row[1]).longValue();
            long present = ((Number) row[2]).longValue();
            subjectAtt.put(subject, total > 0 ? (present * 100.0 / total) : 0.0);
        }

        List<AIPredictionDTO.SubjectPerformance> perfs = new ArrayList<>();
        for (Map.Entry<String, Double> entry : subjectMarks.entrySet()) {
            String subject = entry.getKey();
            double marksPct = entry.getValue();
            double attPct   = subjectAtt.getOrDefault(subject, 0.0);

            String grade = com.college.erp.entity.Marks.computeGrade(marksPct);
            String status;
            if (marksPct < PASS_THRESHOLD) status = "FAIL";
            else if (marksPct < 50 || attPct < HIGH_RISK_ATTENDANCE) status = "AT_RISK";
            else status = "PASS";

            perfs.add(AIPredictionDTO.SubjectPerformance.builder()
                    .subject(subject)
                    .marksPercentage(Math.round(marksPct * 100.0) / 100.0)
                    .attendancePercentage(Math.round(attPct * 100.0) / 100.0)
                    .grade(grade)
                    .status(status)
                    .build());
        }
        return perfs;
    }

    private String computeOverallRisk(double attendance, double marks, long recentAbsences) {
        int riskScore = 0;
        if (attendance < CRITICAL_ATTENDANCE) riskScore += 3;
        else if (attendance < HIGH_RISK_ATTENDANCE) riskScore += 2;
        else if (attendance < MED_RISK_ATTENDANCE) riskScore += 1;

        if (marks < PASS_THRESHOLD) riskScore += 3;
        else if (marks < 50) riskScore += 2;
        else if (marks < GOOD_MARKS) riskScore += 1;

        if (recentAbsences > 10) riskScore += 2;
        else if (recentAbsences > 5) riskScore += 1;

        if (riskScore >= 5) return "HIGH";
        if (riskScore >= 3) return "MEDIUM";
        return "LOW";
    }

    private List<String> generateSuggestions(AIPredictionDTO dto) {
        List<String> suggestions = new ArrayList<>();

        if (dto.getAttendancePercentage() < HIGH_RISK_ATTENDANCE) {
            int classesNeeded = computeClassesNeeded(
                    dto.getAttendancePercentage(), dto.getConsecutiveAbsences());
            suggestions.add("📅 Attend " + classesNeeded + " consecutive classes to reach 75% attendance threshold.");
        }

        if (dto.getAverageMarks() < GOOD_MARKS) {
            suggestions.add("📚 Focus on subjects with below-average scores. Target 15% improvement in the next exam.");
        }

        if ("DECLINING".equals(dto.getAttendanceTrend())) {
            suggestions.add("⚠️ Your attendance has been declining. Meet your faculty advisor immediately.");
        }

        if ("DECLINING".equals(dto.getPerformanceTrend())) {
            suggestions.add("📉 Academic performance is declining. Consider joining study groups and extra classes.");
        }

        if (dto.getConsecutiveAbsences() > 5) {
            suggestions.add("🏥 Multiple recent absences detected. If due to health, submit medical certificate to admin.");
        }

        if (dto.getSubjectPerformances() != null) {
            dto.getSubjectPerformances().stream()
                .filter(s -> "AT_RISK".equals(s.getStatus()))
                .forEach(s -> suggestions.add("🔴 " + s.getSubject() + " is at risk (" +
                        s.getMarksPercentage() + "%). Request extra tutoring sessions."));
        }

        if (suggestions.isEmpty()) {
            suggestions.add("✅ Keep up the great work! Maintain consistent attendance and performance.");
            suggestions.add("🎯 Aim for O grade in all subjects this semester.");
        }

        return suggestions;
    }

    private List<String> generateWarnings(AIPredictionDTO dto) {
        List<String> warnings = new ArrayList<>();

        if (dto.getAttendancePercentage() < CRITICAL_ATTENDANCE) {
            warnings.add("🚨 CRITICAL: Attendance below 60%. You may be barred from examinations!");
        } else if (dto.getAttendancePercentage() < HIGH_RISK_ATTENDANCE) {
            warnings.add("⚠️ WARNING: Attendance below 75%. Immediate improvement required.");
        }

        if (dto.getAverageMarks() < PASS_THRESHOLD) {
            warnings.add("🚨 CRITICAL: Average marks below passing threshold (40%). Risk of failing semester!");
        }

        if (dto.getSubjectPerformances() != null) {
            long failCount = dto.getSubjectPerformances().stream()
                    .filter(s -> "FAIL".equals(s.getStatus())).count();
            if (failCount > 0) {
                warnings.add("📛 " + failCount + " subject(s) showing failing marks. Immediate remediation needed.");
            }
        }

        return warnings;
    }

    private int computeClassesNeeded(double currentPct, int absences) {
        // Simple algorithm: how many consecutive PRESENT needed to reach 75%
        // Assuming roughly 40 total classes as base
        int totalEstimated = 40;
        int presentNow = (int) (currentPct / 100.0 * totalEstimated);
        int needed = 0;
        double pct = currentPct;

        while (pct < 75.0 && needed < 100) {
            needed++;
            presentNow++;
            totalEstimated++;
            pct = (presentNow * 100.0) / totalEstimated;
        }
        return needed;
    }
}
