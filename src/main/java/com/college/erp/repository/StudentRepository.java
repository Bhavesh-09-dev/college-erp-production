package com.college.erp.repository;

import com.college.erp.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEnrollmentNo(String enrollmentNo);

    Optional<Student> findByEmail(String email);

    boolean existsByEnrollmentNo(String enrollmentNo);

    boolean existsByEmail(String email);

    List<Student> findByDepartment(String department);

    List<Student> findBySemester(Integer semester);

    List<Student> findByDepartmentAndSemester(String department, Integer semester);

    List<Student> findByActive(boolean active);

    long countByActive(boolean active);

    long countByDepartment(String department);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.enrollmentNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Student> searchStudents(@Param("query") String query, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.active = true ORDER BY s.createdAt DESC")
    List<Student> findRecentStudents(Pageable pageable);

    @Query("SELECT DISTINCT s.department FROM Student s ORDER BY s.department")
    List<String> findAllDepartments();

    @Query("SELECT s.department, COUNT(s) FROM Student s GROUP BY s.department")
    List<Object[]> countStudentsByDepartment();

    @Query("SELECT s.semester, COUNT(s) FROM Student s GROUP BY s.semester ORDER BY s.semester")
    List<Object[]> countStudentsBySemester();

    Page<Student> findByDepartmentContainingIgnoreCase(String department, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.department = :dept AND s.semester = :sem AND s.active = true")
    List<Student> findByDepartmentAndSemesterAndActive(@Param("dept") String department,
                                                        @Param("sem") Integer semester);

    Optional<Student> findByUserId(Long userId);
}
