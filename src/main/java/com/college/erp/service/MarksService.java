package com.college.erp.service;

import com.college.erp.dto.MarksDTO;

import java.util.List;
import java.util.Map;

public interface MarksService {
    MarksDTO addMarks(MarksDTO dto);
    MarksDTO updateMarks(Long id, MarksDTO dto);
    void deleteMarks(Long id);
    MarksDTO getMarksById(Long id);
    List<MarksDTO> getMarksByStudent(Long studentId);
    List<MarksDTO> getMarksByStudentAndSemester(Long studentId, Integer semester);
    Map<String, Double> getSubjectWisePerformance(Long studentId);
    double getAverageMarks(Long studentId);
    double getAverageMarksByStudentAndSemester(Long studentId, Integer semester);
    Map<String, Long> getGradeDistribution();
    Map<String, Double> getAverageMarksBySubject();
    Map<String, Double> getAverageMarksByDepartment();
    List<String> getAllExamTypes();
    String generateSemesterResult(Long studentId, Integer semester);
}
