package com.college.erp.controller;

import com.college.erp.dto.*;
import com.college.erp.entity.Notice;
import com.college.erp.service.*;
import com.college.erp.service.impl.AIService;
import com.college.erp.service.impl.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final NoticeService noticeService;
    private final DashboardService dashboardService;
    private final AIService aiService;

    // ─── Dashboard ───────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardDTO dashboard = dashboardService.buildAdminDashboard();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    // ─── Student Management ─────────────────────────────────────────────────

    @GetMapping("/students")
    public String listStudents(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String search,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<StudentDTO> students = (search != null && !search.isBlank())
                ? studentService.searchStudents(search, pageable)
                : studentService.getAllStudents(pageable);

        model.addAttribute("students", students);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "students");
        return "admin/students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "students");
        return "admin/student-form";
    }

    @PostMapping("/students")
    public String createStudent(@Valid @ModelAttribute("student") StudentDTO dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", studentService.getAllDepartments());
            model.addAttribute("isEdit", false);
            return "admin/student-form";
        }
        studentService.createStudent(dto);
        return "redirect:/admin/students?success=created";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "students");
        return "admin/student-form";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id,
                                  @Valid @ModelAttribute("student") StudentDTO dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", studentService.getAllDepartments());
            model.addAttribute("isEdit", true);
            return "admin/student-form";
        }
        studentService.updateStudent(id, dto);
        return "redirect:/admin/students?success=updated";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        StudentDTO student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("marks", marksService.getMarksByStudent(id));
        model.addAttribute("attendanceSummary", attendanceService.getAttendanceSummaryForStudent(id));
        model.addAttribute("subjectAttendance", attendanceService.getAttendanceBySubjectForStudent(id));
        model.addAttribute("activePage", "students");
        return "admin/student-view";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin/students?success=deleted";
    }

    // ─── Faculty Management ─────────────────────────────────────────────────

    @GetMapping("/faculty")
    public String listFaculty(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String search,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<FacultyDTO> faculty = (search != null && !search.isBlank())
                ? facultyService.searchFaculty(search, pageable)
                : facultyService.getAllFaculty(pageable);

        model.addAttribute("facultyList", faculty);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "faculty");
        return "admin/faculty";
    }

    @GetMapping("/faculty/new")
    public String newFacultyForm(Model model) {
        model.addAttribute("faculty", new FacultyDTO());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "faculty");
        return "admin/faculty-form";
    }

    @PostMapping("/faculty")
    public String createFaculty(@Valid @ModelAttribute("faculty") FacultyDTO dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/faculty-form";
        }
        facultyService.createFaculty(dto);
        return "redirect:/admin/faculty?success=created";
    }

    @GetMapping("/faculty/{id}/edit")
    public String editFacultyForm(@PathVariable Long id, Model model) {
        model.addAttribute("faculty", facultyService.getFacultyById(id));
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "faculty");
        return "admin/faculty-form";
    }

    @PostMapping("/faculty/{id}")
    public String updateFaculty(@PathVariable Long id,
                                  @Valid @ModelAttribute("faculty") FacultyDTO dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/faculty-form";
        }
        facultyService.updateFaculty(id, dto);
        return "redirect:/admin/faculty?success=updated";
    }

    @PostMapping("/faculty/{id}/delete")
    public String deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return "redirect:/admin/faculty?success=deleted";
    }

    // ─── Notice Board ────────────────────────────────────────────────────────

    @GetMapping("/notices")
    public String listNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        model.addAttribute("activePage", "notices");
        return "admin/notices";
    }

    @GetMapping("/notices/new")
    public String newNoticeForm(Model model) {
        model.addAttribute("notice", new NoticeDTO());
        model.addAttribute("priorities", Notice.Priority.values());
        model.addAttribute("audiences", Notice.TargetAudience.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "notices");
        return "admin/notice-form";
    }

    @PostMapping("/notices")
    public String createNotice(@Valid @ModelAttribute("notice") NoticeDTO dto,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Notice.Priority.values());
            model.addAttribute("audiences", Notice.TargetAudience.values());
            model.addAttribute("isEdit", false);
            return "admin/notice-form";
        }
        dto.setPostedBy("Administrator");
        noticeService.createNotice(dto);
        return "redirect:/admin/notices?success=created";
    }

    @GetMapping("/notices/{id}/edit")
    public String editNoticeForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(id));
        model.addAttribute("priorities", Notice.Priority.values());
        model.addAttribute("audiences", Notice.TargetAudience.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "notices");
        return "admin/notice-form";
    }

    @PostMapping("/notices/{id}")
    public String updateNotice(@PathVariable Long id,
                                 @Valid @ModelAttribute("notice") NoticeDTO dto,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Notice.Priority.values());
            model.addAttribute("audiences", Notice.TargetAudience.values());
            model.addAttribute("isEdit", true);
            return "admin/notice-form";
        }
        noticeService.updateNotice(id, dto);
        return "redirect:/admin/notices?success=updated";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin/notices?success=deleted";
    }

    // ─── AI Analytics ────────────────────────────────────────────────────────

    @GetMapping("/ai-insights")
    public String aiInsights(Model model) {
        List<AIPredictionDTO> atRisk = aiService.getAtRiskStudents();
        List<AIPredictionDTO> all = aiService.getAllPredictions();
        model.addAttribute("atRiskStudents", atRisk);
        model.addAttribute("allPredictions", all);
        model.addAttribute("activePage", "ai-insights");
        return "admin/ai-insights";
    }

    @GetMapping("/ai-insights/{studentId}")
    public String studentAIInsight(@PathVariable Long studentId, Model model) {
        model.addAttribute("prediction", aiService.predictForStudent(studentId));
        model.addAttribute("activePage", "ai-insights");
        return "admin/ai-student-detail";
    }

    // ─── Marks Management (Admin can also manage) ───────────────────────────

    @GetMapping("/marks")
    public String marksManagement(Model model) {
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("activePage", "marks");
        return "admin/marks";
    }

    @GetMapping("/marks/student/{studentId}")
    public String studentMarks(@PathVariable Long studentId, Model model) {
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("marks", marksService.getMarksByStudent(studentId));
        model.addAttribute("activePage", "marks");
        return "admin/student-marks";
    }
}
