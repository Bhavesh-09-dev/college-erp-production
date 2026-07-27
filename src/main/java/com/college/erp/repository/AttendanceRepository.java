package com.college.erp.repository;

import com.college.erp.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByStudentIdAndSubject(Long studentId, String subject);

    List<Attendance> findByStudentIdAndSemester(Long studentId, String semester);

    List<Attendance> findByFacultyId(Long facultyId);

    List<Attendance> findByAttendanceDate(LocalDate date);

    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId,
                                                              LocalDate startDate,
                                                              LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.subject = :subject")
    long countTotalByStudentAndSubject(@Param("studentId") Long studentId,
                                       @Param("subject") String subject);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.subject = :subject AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("studentId") Long studentId,
                                          @Param("subject") String subject);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    long countPresentByStudent(@Param("studentId") Long studentId);

    @Query("SELECT a.subject, COUNT(a), SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.student.id = :studentId GROUP BY a.subject")
    List<Object[]> getAttendanceStatsBySubject(@Param("studentId") Long studentId);

    @Query("SELECT a.student, COUNT(a), SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.student.department = :dept GROUP BY a.student")
    List<Object[]> getAttendanceByDepartment(@Param("dept") String department);

    @Query("SELECT a.student.id, COUNT(a), SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) " +
           "FROM Attendance a GROUP BY a.student.id")
    List<Object[]> getAllStudentAttendanceSummary();

    @Query("SELECT DISTINCT a.subject FROM Attendance a WHERE a.student.department = :dept ORDER BY a.subject")
    List<String> findSubjectsByDepartment(@Param("dept") String department);

    @Query("SELECT DISTINCT a.subject FROM Attendance a ORDER BY a.subject")
    List<String> findAllSubjects();

    // For monthly trend
    @Query(value = "SELECT MONTH(attendance_date) as month, YEAR(attendance_date) as year, " +
                   "COUNT(*) as total, SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) as present " +
                   "FROM attendance WHERE YEAR(attendance_date) = :year " +
                   "GROUP BY YEAR(attendance_date), MONTH(attendance_date) ORDER BY month",
           nativeQuery = true)
    List<Object[]> getMonthlyAttendanceTrend(@Param("year") int year);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.status = 'ABSENT' AND a.attendanceDate >= :since ORDER BY a.attendanceDate DESC")
    long countRecentAbsences(@Param("studentId") Long studentId, @Param("since") LocalDate since);

    boolean existsByStudentIdAndAttendanceDateAndSubject(Long studentId, LocalDate date, String subject);
}
