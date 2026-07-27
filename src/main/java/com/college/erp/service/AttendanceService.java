package com.college.erp.service;

import com.college.erp.dto.AttendanceDTO;
import com.college.erp.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    AttendanceDTO markAttendance(AttendanceDTO dto);

    List<AttendanceDTO> markBulkAttendance(List<AttendanceDTO> dtos);

    AttendanceDTO updateAttendance(Long id, AttendanceDTO dto);

    void deleteAttendance(Long id);

    AttendanceDTO getAttendanceById(Long id);

    List<AttendanceDTO> getAttendanceByStudent(Long studentId);

    List<AttendanceDTO> getAttendanceByStudentAndSubject(Long studentId, String subject);

    List<AttendanceDTO> getAttendanceByDate(LocalDate date);

    List<AttendanceDTO> getAttendanceByDateRange(LocalDate start, LocalDate end);

    double getAttendancePercentage(Long studentId);

    double getAttendancePercentageBySubject(Long studentId, String subject);

    Map<String, Double> getAttendanceBySubjectForStudent(Long studentId);

    List<AttendanceDTO> getStudentsAtRisk();

    List<String> getAllSubjects();

    AttendanceDTO getAttendanceSummaryForStudent(Long studentId);
}
