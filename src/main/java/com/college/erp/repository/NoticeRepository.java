package com.college.erp.repository;

import com.college.erp.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByActiveOrderByCreatedAtDesc(boolean active);

    List<Notice> findByTargetAudienceInAndActiveOrderByCreatedAtDesc(
            List<Notice.TargetAudience> audiences, boolean active);

    List<Notice> findByPriorityAndActiveOrderByCreatedAtDesc(
            Notice.Priority priority, boolean active);

    @Query("SELECT n FROM Notice n WHERE n.active = true AND " +
           "(n.expiryDate IS NULL OR n.expiryDate >= CURRENT_DATE) ORDER BY " +
           "CASE n.priority WHEN 'URGENT' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'NORMAL' THEN 3 ELSE 4 END, " +
           "n.createdAt DESC")
    List<Notice> findActiveNoticesOrderedByPriority();

    @Query("SELECT n FROM Notice n WHERE n.active = true AND " +
           "(n.targetAudience = 'ALL' OR n.targetAudience = 'STUDENTS') AND " +
           "(n.expiryDate IS NULL OR n.expiryDate >= CURRENT_DATE) ORDER BY n.createdAt DESC")
    List<Notice> findStudentNotices();

    @Query("SELECT n FROM Notice n WHERE n.active = true AND " +
           "(n.targetAudience = 'ALL' OR n.targetAudience = 'FACULTY') AND " +
           "(n.expiryDate IS NULL OR n.expiryDate >= CURRENT_DATE) ORDER BY n.createdAt DESC")
    List<Notice> findFacultyNotices();

    long countByActive(boolean active);

    @Query("SELECT n FROM Notice n ORDER BY n.createdAt DESC")
    List<Notice> findRecentNotices(org.springframework.data.domain.Pageable pageable);
}
