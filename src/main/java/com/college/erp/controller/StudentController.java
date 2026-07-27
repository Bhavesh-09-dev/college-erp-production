package com.college.erp.controller;

import com.college.erp.dto.AIPredictionDTO;
import com.college.erp.dto.StudentDTO;
import com.college.erp.service.*;
import com.college.erp.service.impl.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final NoticeService noticeService;
    private final AIService aiService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());

        model.addAttribute("student", student);
        model.addAttribute("attendanceSummary", attendanceService.getAttendanceSummaryForStudent(student.getId()));
        model.addAttribute("subjectAttendance", attendanceService.getAttendanceBySubjectForStudent(student.getId()));
        model.addAttribute("subjectMarks", marksService.getSubjectWisePerformance(student.getId()));
        model.addAttribute("notices", noticeService.getStudentNotices());
        model.addAttribute("activePage", "dashboard");
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        model.addAttribute("student", student);
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "profile");
        return "student/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        model.addAttribute("student", student);
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "profile");
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication auth,
                                  @Valid @ModelAttribute("student") StudentDTO dto,
                                  BindingResult result, Model model) {
        StudentDTO existing = studentService.getStudentByUsername(auth.getName());

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "student/profile";
        }

        // Only allow updating contact info, not academic details
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setGuardianName(dto.getGuardianName());
        existing.setGuardianPhone(dto.getGuardianPhone());
        existing.setDateOfBirth(dto.getDateOfBirth());

        studentService.updateStudent(existing.getId(), existing);
        return "redirect:/student/profile?success=updated";
    }

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        model.addAttribute("student", student);
        model.addAttribute("summary", attendanceService.getAttendanceSummaryForStudent(student.getId()));
        model.addAttribute("subjectAttendance", attendanceService.getAttendanceBySubjectForStudent(student.getId()));
        model.addAttribute("attendanceList", attendanceService.getAttendanceByStudent(student.getId()));
        model.addAttribute("activePage", "attendance");
        return "student/attendance";
    }

    @GetMapping("/marks")
    public String marks(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        model.addAttribute("student", student);
        model.addAttribute("marks", marksService.getMarksByStudent(student.getId()));
        model.addAttribute("subjectPerformance", marksService.getSubjectWisePerformance(student.getId()));
        model.addAttribute("activePage", "marks");
        return "student/marks";
    }

    @GetMapping("/result")
    public String result(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        model.addAttribute("student", student);

        Map<Integer, String> semesterResults = new java.util.LinkedHashMap<>();
        Map<Integer, Double> semesterAverages = new java.util.LinkedHashMap<>();
        for (int sem = 1; sem <= student.getSemester(); sem++) {
            String result = marksService.generateSemesterResult(student.getId(), sem);
            double avg = marksService.getAverageMarksByStudentAndSemester(student.getId(), sem);
            if (!"NO_DATA".equals(result)) {
                semesterResults.put(sem, result);
                semesterAverages.put(sem, avg);
            }
        }

        model.addAttribute("semesterResults", semesterResults);
        model.addAttribute("semesterAverages", semesterAverages);
        model.addAttribute("overallAverage", marksService.getAverageMarks(student.getId()));
        model.addAttribute("activePage", "result");
        return "student/result";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeService.getStudentNotices());
        model.addAttribute("activePage", "notices");
        return "student/notices";
    }

    @GetMapping("/ai-insights")
    public String aiInsights(Authentication auth, Model model) {
        StudentDTO student = studentService.getStudentByUsername(auth.getName());
        AIPredictionDTO prediction = aiService.predictForStudent(student.getId());
        model.addAttribute("student", student);
        model.addAttribute("prediction", prediction);
        model.addAttribute("activePage", "ai-insights");
        return "student/ai-insights";
    }
}
