package com.ryuqq.marketplace.application.notice.dto.response;

import java.time.Instant;
import java.util.List;

/** 고시정보 카테고리 조회 결과 DTO (하위 필드 포함). */
public record NoticeCategoryResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        String targetCategoryGroup,
        boolean active,
        List<NoticeFieldResult> fields,
        Instant createdAt) {}
