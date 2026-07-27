package com.college.erp.service.impl;

import com.college.erp.dto.FacultyDTO;
import com.college.erp.entity.Faculty;
import com.college.erp.entity.User;
import com.college.erp.exception.DuplicateResourceException;
import com.college.erp.exception.ResourceNotFoundException;
import com.college.erp.repository.FacultyRepository;
import com.college.erp.repository.UserRepository;
import com.college.erp.service.FacultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public FacultyDTO createFaculty(FacultyDTO dto) {
        if (facultyRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new DuplicateResourceException("Faculty with employee ID " + dto.getEmployeeId() + " already exists");
        }
        if (facultyRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Faculty with email " + dto.getEmail() + " already exists");
        }

        String username = dto.getUsername() != null ? dto.getUsername()
                : dto.getEmployeeId().toLowerCase();
        String password = dto.getPassword() != null ? dto.getPassword() : "faculty123";

        User user = User.builder()
                .username(username)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(password))
                .role(User.Role.ROLE_FACULTY)
                .fullName(dto.getFirstName() + " " + dto.getLastName())
                .phone(dto.getPhone())
                .enabled(true)
                .build();
        userRepository.save(user);

        Faculty faculty = buildFacultyFromDTO(dto);
        faculty.setUser(user);
        Faculty saved = facultyRepository.save(faculty);
        log.info("Faculty created: {}", saved.getEmployeeId());
        return FacultyDTO.fromEntity(saved);
    }

    @Override
    public FacultyDTO updateFaculty(Long id, FacultyDTO dto) {
        Faculty faculty = findFacultyEntityById(id);

        if (!faculty.getEmail().equals(dto.getEmail()) &&
                facultyRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email " + dto.getEmail() + " already in use");
        }

        faculty.setFirstName(dto.getFirstName());
        faculty.setLastName(dto.getLastName());
        faculty.setEmail(dto.getEmail());
        faculty.setPhone(dto.getPhone());
        faculty.setDepartment(dto.getDepartment());
        faculty.setDesignation(dto.getDesignation());
        faculty.setSpecialization(dto.getSpecialization());
        faculty.setDateOfJoining(dto.getDateOfJoining());
        faculty.setDateOfBirth(dto.getDateOfBirth());
        faculty.setAddress(dto.getAddress());
        faculty.setQualification(dto.getQualification());
        faculty.setExperienceYears(dto.getExperienceYears());
        faculty.setActive(dto.isActive());
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            faculty.setGender(Faculty.Gender.valueOf(dto.getGender()));
        }

        if (faculty.getUser() != null) {
            faculty.getUser().setEmail(dto.getEmail());
            faculty.getUser().setFullName(dto.getFirstName() + " " + dto.getLastName());
        }

        Faculty updated = facultyRepository.save(faculty);
        log.info("Faculty updated: {}", updated.getEmployeeId());
        return FacultyDTO.fromEntity(updated);
    }

    @Override
    public void deleteFaculty(Long id) {
        Faculty faculty = findFacultyEntityById(id);
        faculty.setActive(false);
        if (faculty.getUser() != null) {
            faculty.getUser().setEnabled(false);
        }
        facultyRepository.save(faculty);
        log.info("Faculty soft-deleted: {}", faculty.getEmployeeId());
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyDTO getFacultyById(Long id) {
        return FacultyDTO.fromEntity(findFacultyEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyDTO getFacultyByEmployeeId(String employeeId) {
        return FacultyDTO.fromEntity(
                facultyRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + employeeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacultyDTO> getAllFaculty(Pageable pageable) {
        return facultyRepository.findAll(pageable).map(FacultyDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacultyDTO> searchFaculty(String query, Pageable pageable) {
        return facultyRepository.searchFaculty(query, pageable).map(FacultyDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyDTO> getFacultyByDepartment(String department) {
        return facultyRepository.findByDepartment(department).stream()
                .map(FacultyDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return facultyRepository.findAllDepartments();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalFacultyCount() {
        return facultyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveFacultyCount() {
        return facultyRepository.countByActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyDTO getFacultyByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Faculty faculty = facultyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found for: " + username));
        return FacultyDTO.fromEntity(faculty);
    }

    private Faculty findFacultyEntityById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));
    }

    private Faculty buildFacultyFromDTO(FacultyDTO dto) {
        Faculty f = new Faculty();
        f.setEmployeeId(dto.getEmployeeId());
        f.setFirstName(dto.getFirstName());
        f.setLastName(dto.getLastName());
        f.setEmail(dto.getEmail());
        f.setPhone(dto.getPhone());
        f.setDepartment(dto.getDepartment());
        f.setDesignation(dto.getDesignation());
        f.setSpecialization(dto.getSpecialization());
        f.setDateOfJoining(dto.getDateOfJoining());
        f.setDateOfBirth(dto.getDateOfBirth());
        f.setAddress(dto.getAddress());
        f.setQualification(dto.getQualification());
        f.setExperienceYears(dto.getExperienceYears());
        f.setActive(true);
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            f.setGender(Faculty.Gender.valueOf(dto.getGender()));
        }
        return f;
    }
}
