package com.college.erp.service;

import com.college.erp.dto.StudentDTO;
import com.college.erp.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

    StudentDTO createStudent(StudentDTO dto);

    StudentDTO updateStudent(Long id, StudentDTO dto);

    void deleteStudent(Long id);

    StudentDTO getStudentById(Long id);

    StudentDTO getStudentByEnrollmentNo(String enrollmentNo);

    StudentDTO getStudentByEmail(String email);

    Page<StudentDTO> getAllStudents(Pageable pageable);

    Page<StudentDTO> searchStudents(String query, Pageable pageable);

    List<StudentDTO> getStudentsByDepartment(String department);

    List<StudentDTO> getStudentsBySemester(Integer semester);

    List<StudentDTO> getStudentsByDepartmentAndSemester(String dept, Integer semester);

    List<String> getAllDepartments();

    long getTotalStudentCount();

    long getActiveStudentCount();

    StudentDTO getStudentByUsername(String username);

    boolean existsByEnrollmentNo(String enrollmentNo);

    boolean existsByEmail(String email);
}
