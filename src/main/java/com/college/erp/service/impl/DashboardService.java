package com.college.erp.service.impl;

import com.college.erp.dto.DashboardDTO;
import com.college.erp.dto.NoticeDTO;
import com.college.erp.dto.StudentDTO;
import com.college.erp.repository.*;
import com.college.erp.service.AttendanceService;
import com.college.erp.service.MarksService;
import com.college.erp.service.NoticeService;
import com.college.erp.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final NoticeRepository noticeRepository;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final NoticeService noticeService;

    public DashboardDTO buildAdminDashboard() {
        DashboardDTO dashboard = new DashboardDTO();

        // Counts
        dashboard.setTotalStudents(studentRepository.count());
        dashboard.setTotalFaculty(facultyRepository.count());
        dashboard.setActiveStudents(studentRepository.countByActive(true));
        dashboard.setActiveFaculty(facultyRepository.countByActive(true));
        dashboard.setTotalNotices(noticeRepository.countByActive(true));

        // Attendance stats
        buildAttendanceStats(dashboard);

        // Marks stats
        buildMarksStats(dashboard);

        // Distribution
        buildDistributions(dashboard);

        // Recent data
        dashboard.setRecentStudents(
            studentRepository.findRecentStudents(PageRequest.of(0, 5))
                .stream().map(StudentDTO::fromEntity).toList()
        );
        dashboard.setRecentNotices(
            noticeService.getActiveNotices().stream().limit(5).toList()
        );

        // At-risk students
        List<StudentDTO> atRisk = attendanceService.getStudentsAtRisk().stream()
            .map(a -> {
                StudentDTO s = new StudentDTO();
                s.setId(a.getStudentId());
                s.setFirstName(a.getStudentName());
                s.setEnrollmentNo(a.getEnrollmentNo());
                s.setAttendancePercentage(a.getAttendancePercentage());
                return s;
            }).toList();
        dashboard.setAtRiskStudents(atRisk);
        dashboard.setStudentsAtRisk(atRisk.size());

        // Monthly trend
        buildMonthlyTrend(dashboard);

        // Semester trend
        buildSemesterTrend(dashboard);

        return dashboard;
    }

    private void buildAttendanceStats(DashboardDTO d) {
        List<Object[]> summaries = attendanceRepository.getAllStudentAttendanceSummary();
        if (summaries.isEmpty()) {
            d.setOverallAttendancePercentage(0.0);
            return;
        }
        double totalPct = summaries.stream()
            .mapToDouble(row -> {
                long total = ((Number) row[1]).longValue();
                long present = ((Number) row[2]).longValue();
                return total > 0 ? (present * 100.0 / total) : 0.0;
            }).average().orElse(0.0);
        d.setOverallAttendancePercentage(Math.round(totalPct * 100.0) / 100.0);

        long inDanger = summaries.stream()
            .filter(row -> {
                long total = ((Number) row[1]).longValue();
                long present = ((Number) row[2]).longValue();
                return total > 0 && (present * 100.0 / total) < 60.0;
            }).count();
        d.setStudentsInDanger(inDanger);
    }

    private void buildMarksStats(DashboardDTO d) {
        Double overall = marksRepository.getOverallAveragePercentage();
        d.setOverallAverageMarks(overall != null ? Math.round(overall * 100.0) / 100.0 : 0.0);
        d.setGradeDistribution(marksService.getGradeDistribution());
        d.setAvgMarksByDepartment(marksService.getAverageMarksByDepartment());
        d.setAvgMarksBySubject(marksService.getAverageMarksBySubject());
    }

    private void buildDistributions(DashboardDTO d) {
        Map<String, Long> byDept = new LinkedHashMap<>();
        for (Object[] row : studentRepository.countStudentsByDepartment()) {
            byDept.put((String) row[0], ((Number) row[1]).longValue());
        }
        d.setStudentsByDepartment(byDept);

        Map<String, Long> bySem = new LinkedHashMap<>();
        for (Object[] row : studentRepository.countStudentsBySemester()) {
            bySem.put("Sem " + row[0], ((Number) row[1]).longValue());
        }
        d.setStudentsBySemester(bySem);
    }

    private void buildMonthlyTrend(DashboardDTO d) {
        int year = LocalDate.now().getYear();
        List<Object[]> trend = attendanceRepository.getMonthlyAttendanceTrend(year);

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : trend) {
            int month = ((Number) row[0]).intValue();
            long total = ((Number) row[2]).longValue();
            long present = ((Number) row[3]).longValue();
            double pct = total > 0 ? (present * 100.0 / total) : 0.0;
            labels.add(months[month - 1]);
            values.add(Math.round(pct * 100.0) / 100.0);
        }
        d.setMonthLabels(labels);
        d.setMonthlyAttendance(values);
    }

    private void buildSemesterTrend(DashboardDTO d) {
        List<Object[]> rows = marksRepository.getAverageMarksBySemester();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Object[] row : rows) {
            labels.add("Sem " + row[0]);
            Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            values.add(Math.round(avg * 100.0) / 100.0);
        }
        d.setSemesterLabels(labels);
        d.setSemesterAvgMarks(values);
    }
}
