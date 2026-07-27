package com.college.erp.service;

import com.college.erp.dto.FacultyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FacultyService {

    FacultyDTO createFaculty(FacultyDTO dto);

    FacultyDTO updateFaculty(Long id, FacultyDTO dto);

    void deleteFaculty(Long id);

    FacultyDTO getFacultyById(Long id);

    FacultyDTO getFacultyByEmployeeId(String employeeId);

    Page<FacultyDTO> getAllFaculty(Pageable pageable);

    Page<FacultyDTO> searchFaculty(String query, Pageable pageable);

    List<FacultyDTO> getFacultyByDepartment(String department);

    List<String> getAllDepartments();

    long getTotalFacultyCount();

    long getActiveFacultyCount();

    FacultyDTO getFacultyByUsername(String username);
}
