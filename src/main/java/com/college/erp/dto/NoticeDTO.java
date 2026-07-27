package com.college.erp.dto;

import com.college.erp.entity.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Notice.Priority priority;
    private Notice.TargetAudience targetAudience;
    private String postedBy;
    private LocalDate expiryDate;
    private boolean active;
    private String department;
    private LocalDateTime createdAt;

    public static NoticeDTO fromEntity(Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .priority(notice.getPriority())
                .targetAudience(notice.getTargetAudience())
                .postedBy(notice.getPostedBy())
                .expiryDate(notice.getExpiryDate())
                .active(notice.isActive())
                .department(notice.getDepartment())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
