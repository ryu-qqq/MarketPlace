package com.ryuqq.marketplace.application.inboundcategorymapping.dto.command;

/** 외부 카테고리 매핑 등록 Command. */
public record RegisterInboundCategoryMappingCommand(
        long inboundSourceId,
        String externalCategoryCode,
        String externalCategoryName,
        long internalCategoryId) {}
