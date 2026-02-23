package com.ryuqq.marketplace.application.inboundcategorymapping.dto.command;

/** 외부 카테고리 매핑 수정 Command. */
public record UpdateInboundCategoryMappingCommand(
        long id, String externalCategoryName, long internalCategoryId, String status) {}
