package com.college.erp.repository;

import com.college.erp.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    List<Marks> findByStudentId(Long studentId);

    List<Marks> findByStudentIdAndSemester(Long studentId, Integer semester);

    List<Marks> findByStudentIdAndSubject(Long studentId, String subject);

    List<Marks> findBySemesterAndDepartment(Integer semester, String department);

    Optional<Marks> findByStudentIdAndSubjectAndSemesterAndExamType(
            Long studentId, String subject, Integer semester, String examType);

    @Query("SELECT AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m WHERE m.student.id = :studentId")
    Double getAveragePercentageByStudent(@Param("studentId") Long studentId);

    @Query("SELECT AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m " +
           "WHERE m.student.id = :studentId AND m.semester = :semester")
    Double getAveragePercentageByStudentAndSemester(@Param("studentId") Long studentId,
                                                     @Param("semester") Integer semester);

    @Query("SELECT m.subject, AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m " +
           "GROUP BY m.subject ORDER BY m.subject")
    List<Object[]> getAverageMarksBySubject();

    @Query("SELECT m.subject, AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m " +
           "WHERE m.student.department = :dept GROUP BY m.subject")
    List<Object[]> getAverageMarksBySubjectAndDepartment(@Param("dept") String department);

    @Query("SELECT m.grade, COUNT(m) FROM Marks m GROUP BY m.grade ORDER BY m.grade")
    List<Object[]> getGradeDistribution();

    @Query("SELECT AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m")
    Double getOverallAveragePercentage();

    @Query("SELECT m.student.department, AVG((m.marksObtained / m.maxMarks) * 100) " +
           "FROM Marks m GROUP BY m.student.department")
    List<Object[]> getAverageMarksByDepartment();

    @Query("SELECT m.semester, AVG((m.marksObtained / m.maxMarks) * 100) FROM Marks m " +
           "GROUP BY m.semester ORDER BY m.semester")
    List<Object[]> getAverageMarksBySemester();

    @Query("SELECT m FROM Marks m WHERE m.student.id = :studentId ORDER BY m.semester, m.subject")
    List<Marks> findByStudentIdOrderBySemesterAndSubject(@Param("studentId") Long studentId);

    @Query("SELECT DISTINCT m.subject FROM Marks m WHERE m.student.department = :dept ORDER BY m.subject")
    List<String> findSubjectsByDepartment(@Param("dept") String department);

    @Query("SELECT DISTINCT m.examType FROM Marks m ORDER BY m.examType")
    List<String> findAllExamTypes();

    boolean existsByStudentIdAndSubjectAndSemesterAndExamType(
            Long studentId, String subject, Integer semester, String examType);
}
