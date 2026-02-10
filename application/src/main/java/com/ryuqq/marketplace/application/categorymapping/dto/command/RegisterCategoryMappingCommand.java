package com.ryuqq.marketplace.application.categorymapping.dto.command;

/** 카테고리 매핑 등록 커맨드 DTO. */
public record RegisterCategoryMappingCommand(
        Long salesChannelCategoryId, Long internalCategoryId) {}
