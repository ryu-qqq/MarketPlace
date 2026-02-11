package com.ryuqq.marketplace.application.canonicaloption.dto.response;

/** 캐노니컬 옵션 값 조회 결과 DTO. */
public record CanonicalOptionValueResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        int sortOrder) {}
