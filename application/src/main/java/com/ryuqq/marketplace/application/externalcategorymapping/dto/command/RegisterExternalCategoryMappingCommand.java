package com.ryuqq.marketplace.application.externalcategorymapping.dto.command;

/** 외부 카테고리 매핑 등록 Command. */
public record RegisterExternalCategoryMappingCommand(
        long externalSourceId,
        String externalCategoryCode,
        String externalCategoryName,
        long internalCategoryId) {}
