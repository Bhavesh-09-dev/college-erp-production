package com.college.erp.service.impl;

import com.college.erp.dto.NoticeDTO;
import com.college.erp.entity.Notice;
import com.college.erp.exception.ResourceNotFoundException;
import com.college.erp.repository.NoticeRepository;
import com.college.erp.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeDTO createNotice(NoticeDTO dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .priority(dto.getPriority() != null ? dto.getPriority() : Notice.Priority.NORMAL)
                .targetAudience(dto.getTargetAudience() != null ? dto.getTargetAudience() : Notice.TargetAudience.ALL)
                .postedBy(dto.getPostedBy())
                .expiryDate(dto.getExpiryDate())
                .department(dto.getDepartment())
                .active(true)
                .build();
        return NoticeDTO.fromEntity(noticeRepository.save(notice));
    }

    @Override
    public NoticeDTO updateNotice(Long id, NoticeDTO dto) {
        Notice notice = findById(id);
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        if (dto.getPriority() != null) notice.setPriority(dto.getPriority());
        if (dto.getTargetAudience() != null) notice.setTargetAudience(dto.getTargetAudience());
        notice.setExpiryDate(dto.getExpiryDate());
        notice.setDepartment(dto.getDepartment());
        notice.setActive(dto.isActive());
        return NoticeDTO.fromEntity(noticeRepository.save(notice));
    }

    @Override
    public void deleteNotice(Long id) {
        Notice notice = findById(id);
        notice.setActive(false);
        noticeRepository.save(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDTO getNoticeById(Long id) {
        return NoticeDTO.fromEntity(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(NoticeDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDTO> getActiveNotices() {
        return noticeRepository.findActiveNoticesOrderedByPriority()
                .stream().map(NoticeDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDTO> getStudentNotices() {
        return noticeRepository.findStudentNotices()
                .stream().map(NoticeDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDTO> getFacultyNotices() {
        return noticeRepository.findFacultyNotices()
                .stream().map(NoticeDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveNoticeCount() {
        return noticeRepository.countByActive(true);
    }

    private Notice findById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found: " + id));
    }
}
