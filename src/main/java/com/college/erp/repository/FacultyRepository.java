package com.college.erp.repository;

import com.college.erp.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByEmployeeId(String employeeId);

    Optional<Faculty> findByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    List<Faculty> findByDepartment(String department);

    List<Faculty> findByActive(boolean active);

    long countByActive(boolean active);

    @Query("SELECT f FROM Faculty f WHERE " +
           "LOWER(f.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.department) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Faculty> searchFaculty(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT f.department FROM Faculty f ORDER BY f.department")
    List<String> findAllDepartments();

    Optional<Faculty> findByUserId(Long userId);
}
