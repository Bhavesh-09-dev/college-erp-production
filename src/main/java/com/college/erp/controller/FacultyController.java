package com.college.erp.controller;

import com.college.erp.dto.AttendanceDTO;
import com.college.erp.dto.FacultyDTO;
import com.college.erp.dto.MarksDTO;
import com.college.erp.dto.StudentDTO;
import com.college.erp.entity.Attendance;
import com.college.erp.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final NoticeService noticeService;

    // ─── Dashboard ───────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        FacultyDTO faculty = facultyService.getFacultyByUsername(auth.getName());
        model.addAttribute("faculty", faculty);
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("totalStudents", studentService.getTotalStudentCount());
        model.addAttribute("notices", noticeService.getFacultyNotices());
        model.addAttribute("activePage", "dashboard");
        return "faculty/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        FacultyDTO faculty = facultyService.getFacultyByUsername(auth.getName());
        model.addAttribute("faculty", faculty);
        model.addAttribute("activePage", "profile");
        return "faculty/profile";
    }

    // ─── Attendance Management ──────────────────────────────────────────────

    @GetMapping("/attendance")
    public String attendancePage(@RequestParam(required = false) String department,
                                  @RequestParam(required = false) Integer semester,
                                  @RequestParam(required = false) String subject,
                                  Model model) {
        List<StudentDTO> students = List.of();
        if (department != null && semester != null) {
            students = studentService.getStudentsByDepartmentAndSemester(department, semester);
        }
        model.addAttribute("students", students);
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("statuses", Attendance.Status.values());
        model.addAttribute("activePage", "attendance");
        return "faculty/attendance";
    }

    @PostMapping("/attendance/mark")
    public String markAttendance(@RequestParam Long studentId,
                                  @RequestParam String subject,
                                  @RequestParam String status,
                                  @RequestParam String attendanceDate,
                                  @RequestParam(required = false) String department,
                                  @RequestParam(required = false) Integer semester,
                                  Authentication auth) {
        FacultyDTO faculty = facultyService.getFacultyByUsername(auth.getName());

        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(studentId);
        dto.setFacultyId(faculty.getId());
        dto.setSubject(subject);
        dto.setStatus(Attendance.Status.valueOf(status));
        dto.setAttendanceDate(LocalDate.parse(attendanceDate));
        dto.setSemester(semester != null ? String.valueOf(semester) : null);
        dto.setDepartment(department);

        attendanceService.markAttendance(dto);

        String redirect = "redirect:/faculty/attendance?success=marked";
        if (department != null) redirect += "&department=" + department;
        if (semester != null) redirect += "&semester=" + semester;
        if (subject != null) redirect += "&subject=" + subject;
        return redirect;
    }

    @PostMapping("/attendance/bulk")
    public String markBulkAttendance(@RequestParam("studentIds") List<Long> studentIds,
                                       @RequestParam("statuses") List<String> statuses,
                                       @RequestParam String subject,
                                       @RequestParam String attendanceDate,
                                       @RequestParam(required = false) String department,
                                       @RequestParam(required = false) Integer semester,
                                       Authentication auth) {
        FacultyDTO faculty = facultyService.getFacultyByUsername(auth.getName());
        LocalDate date = LocalDate.parse(attendanceDate);

        for (int i = 0; i < studentIds.size(); i++) {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setStudentId(studentIds.get(i));
            dto.setFacultyId(faculty.getId());
            dto.setSubject(subject);
            dto.setStatus(Attendance.Status.valueOf(statuses.get(i)));
            dto.setAttendanceDate(date);
            dto.setSemester(semester != null ? String.valueOf(semester) : null);
            dto.setDepartment(department);
            attendanceService.markAttendance(dto);
        }

        String redirect = "redirect:/faculty/attendance?success=marked";
        if (department != null) redirect += "&department=" + department;
        if (semester != null) redirect += "&semester=" + semester;
        if (subject != null) redirect += "&subject=" + subject;
        return redirect;
    }

    @GetMapping("/attendance/reports")
    public String attendanceReports(@RequestParam(required = false) String department,
                                      @RequestParam(required = false) Integer semester,
                                      Model model) {
        List<StudentDTO> students = List.of();
        if (department != null && semester != null) {
            students = studentService.getStudentsByDepartmentAndSemester(department, semester);
            students.forEach(s -> {
                double pct = attendanceService.getAttendancePercentage(s.getId());
                s.setAttendancePercentage(pct);
            });
        }
        model.addAttribute("students", students);
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("activePage", "attendance-reports");
        return "faculty/attendance-reports";
    }

    @GetMapping("/attendance/student/{studentId}")
    public String studentAttendanceDetail(@PathVariable Long studentId, Model model) {
        StudentDTO student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("subjectAttendance", attendanceService.getAttendanceBySubjectForStudent(studentId));
        model.addAttribute("attendanceList", attendanceService.getAttendanceByStudent(studentId));
        model.addAttribute("summary", attendanceService.getAttendanceSummaryForStudent(studentId));
        model.addAttribute("activePage", "attendance-reports");
        return "faculty/student-attendance-detail";
    }

    // ─── Marks Management ────────────────────────────────────────────────────

    @GetMapping("/marks")
    public String marksPage(@RequestParam(required = false) String department,
                              @RequestParam(required = false) Integer semester,
                              Model model) {
        List<StudentDTO> students = List.of();
        if (department != null && semester != null) {
            students = studentService.getStudentsByDepartmentAndSemester(department, semester);
        }
        model.addAttribute("students", students);
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("examTypes", List.of("MID_TERM", "END_TERM", "QUIZ", "ASSIGNMENT", "PRACTICAL"));
        model.addAttribute("activePage", "marks");
        return "faculty/marks";
    }

    @PostMapping("/marks/upload")
    public String uploadMarks(@RequestParam Long studentId,
                                @RequestParam String subject,
                                @RequestParam Double marksObtained,
                                @RequestParam Double maxMarks,
                                @RequestParam Integer semester,
                                @RequestParam String examType,
                                @RequestParam(required = false) String academicYear,
                                @RequestParam(required = false) String department) {
        MarksDTO dto = new MarksDTO();
        dto.setStudentId(studentId);
        dto.setSubject(subject);
        dto.setMarksObtained(marksObtained);
        dto.setMaxMarks(maxMarks);
        dto.setSemester(semester);
        dto.setExamType(examType);
        dto.setAcademicYear(academicYear != null ? academicYear : "2024-25");

        marksService.addMarks(dto);

        String redirect = "redirect:/faculty/marks?success=uploaded";
        if (department != null) redirect += "&department=" + department;
        redirect += "&semester=" + semester;
        return redirect;
    }

    @GetMapping("/marks/student/{studentId}")
    public String studentMarks(@PathVariable Long studentId, Model model) {
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("marks", marksService.getMarksByStudent(studentId));
        model.addAttribute("activePage", "marks");
        return "faculty/student-marks";
    }

    @GetMapping("/marks/{id}/edit")
    public String editMarksForm(@PathVariable Long id, Model model) {
        MarksDTO marks = marksService.getMarksById(id);
        model.addAttribute("marks", marks);
        model.addAttribute("examTypes", List.of("MID_TERM", "END_TERM", "QUIZ", "ASSIGNMENT", "PRACTICAL"));
        model.addAttribute("activePage", "marks");
        return "faculty/marks-edit";
    }

    @PostMapping("/marks/{id}")
    public String updateMarks(@PathVariable Long id,
                                @Valid @ModelAttribute("marks") MarksDTO dto,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("examTypes", List.of("MID_TERM", "END_TERM", "QUIZ", "ASSIGNMENT", "PRACTICAL"));
            return "faculty/marks-edit";
        }
        MarksDTO updated = marksService.updateMarks(id, dto);
        return "redirect:/faculty/marks/student/" + updated.getStudentId() + "?success=updated";
    }

    @PostMapping("/marks/{id}/delete")
    public String deleteMarks(@PathVariable Long id, @RequestParam Long studentId) {
        marksService.deleteMarks(id);
        return "redirect:/faculty/marks/student/" + studentId + "?success=deleted";
    }
}
