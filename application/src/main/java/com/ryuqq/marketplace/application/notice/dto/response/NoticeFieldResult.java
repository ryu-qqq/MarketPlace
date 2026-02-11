package com.ryuqq.marketplace.application.notice.dto.response;

/** 고시정보 필드 조회 결과 DTO. */
public record NoticeFieldResult(
        Long id, String fieldCode, String fieldName, boolean required, int sortOrder) {}
