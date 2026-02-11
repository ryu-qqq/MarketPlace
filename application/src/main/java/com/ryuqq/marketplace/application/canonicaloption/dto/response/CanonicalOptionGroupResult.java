package com.ryuqq.marketplace.application.canonicaloption.dto.response;

import java.time.Instant;
import java.util.List;

/** 캐노니컬 옵션 그룹 조회 결과 DTO (하위 옵션 값 포함). */
public record CanonicalOptionGroupResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        boolean active,
        List<CanonicalOptionValueResult> values,
        Instant createdAt) {}
