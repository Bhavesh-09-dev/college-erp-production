package com.college.erp.service.impl;

import com.college.erp.dto.MarksDTO;
import com.college.erp.entity.Marks;
import com.college.erp.entity.Student;
import com.college.erp.exception.ResourceNotFoundException;
import com.college.erp.repository.MarksRepository;
import com.college.erp.repository.StudentRepository;
import com.college.erp.service.MarksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MarksServiceImpl implements MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;

    @Override
    public MarksDTO addMarks(MarksDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));

        Marks marks = Marks.builder()
                .student(student)
                .subject(dto.getSubject())
                .marksObtained(dto.getMarksObtained())
                .maxMarks(dto.getMaxMarks())
                .semester(dto.getSemester())
                .examType(dto.getExamType())
                .academicYear(dto.getAcademicYear())
                .department(student.getDepartment())
                .remarks(dto.getRemarks())
                .build();
        marks.calculateGrade();

        Marks saved = marksRepository.save(marks);
        return MarksDTO.fromEntity(saved);
    }

    @Override
    public MarksDTO updateMarks(Long id, MarksDTO dto) {
        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found: " + id));

        marks.setMarksObtained(dto.getMarksObtained());
        marks.setMaxMarks(dto.getMaxMarks());
        marks.setSubject(dto.getSubject());
        marks.setSemester(dto.getSemester());
        marks.setExamType(dto.getExamType());
        marks.setAcademicYear(dto.getAcademicYear());
        marks.setRemarks(dto.getRemarks());
        marks.calculateGrade();

        return MarksDTO.fromEntity(marksRepository.save(marks));
    }

    @Override
    public void deleteMarks(Long id) {
        if (!marksRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marks not found: " + id);
        }
        marksRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MarksDTO getMarksById(Long id) {
        return MarksDTO.fromEntity(
                marksRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Marks not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksDTO> getMarksByStudent(Long studentId) {
        return marksRepository.findByStudentIdOrderBySemesterAndSubject(studentId)
                .stream().map(MarksDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksDTO> getMarksByStudentAndSemester(Long studentId, Integer semester) {
        return marksRepository.findByStudentIdAndSemester(studentId, semester)
                .stream().map(MarksDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getSubjectWisePerformance(Long studentId) {
        List<Marks> marksList = marksRepository.findByStudentId(studentId);
        Map<String, List<Double>> bySubject = new LinkedHashMap<>();

        for (Marks m : marksList) {
            bySubject.computeIfAbsent(m.getSubject(), k -> new ArrayList<>())
                     .add(m.getPercentage());
        }

        Map<String, Double> result = new LinkedHashMap<>();
        bySubject.forEach((subject, pcts) ->
            result.put(subject, pcts.stream().mapToDouble(d -> d).average().orElse(0.0))
        );
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageMarks(Long studentId) {
        Double avg = marksRepository.getAveragePercentageByStudent(studentId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageMarksByStudentAndSemester(Long studentId, Integer semester) {
        Double avg = marksRepository.getAveragePercentageByStudentAndSemester(studentId, semester);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getGradeDistribution() {
        List<Object[]> rows = marksRepository.getGradeDistribution();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String grade = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            if (grade != null) result.put(grade, count);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getAverageMarksBySubject() {
        List<Object[]> rows = marksRepository.getAverageMarksBySubject();
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String subject = (String) row[0];
            Double avg = row[1] != null ? Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0 : 0.0;
            result.put(subject, avg);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getAverageMarksByDepartment() {
        List<Object[]> rows = marksRepository.getAverageMarksByDepartment();
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String dept = (String) row[0];
            Double avg = row[1] != null ? Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0 : 0.0;
            result.put(dept, avg);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllExamTypes() {
        return marksRepository.findAllExamTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public String generateSemesterResult(Long studentId, Integer semester) {
        List<Marks> marksList = marksRepository.findByStudentIdAndSemester(studentId, semester);
        if (marksList.isEmpty()) return "NO_DATA";

        double avg = marksList.stream()
                .mapToDouble(Marks::getPercentage)
                .average().orElse(0.0);

        long failCount = marksList.stream()
                .filter(m -> m.getPass() != null && !m.getPass())
                .count();

        if (failCount > 0) return "FAIL";

        return Marks.computeGrade(avg);
    }
}
