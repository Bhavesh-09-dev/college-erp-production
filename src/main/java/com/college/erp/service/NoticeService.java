package com.college.erp.service;

import com.college.erp.dto.NoticeDTO;
import com.college.erp.entity.Notice;

import java.util.List;

public interface NoticeService {
    NoticeDTO createNotice(NoticeDTO dto);
    NoticeDTO updateNotice(Long id, NoticeDTO dto);
    void deleteNotice(Long id);
    NoticeDTO getNoticeById(Long id);
    List<NoticeDTO> getAllNotices();
    List<NoticeDTO> getActiveNotices();
    List<NoticeDTO> getStudentNotices();
    List<NoticeDTO> getFacultyNotices();
    long getActiveNoticeCount();
}
