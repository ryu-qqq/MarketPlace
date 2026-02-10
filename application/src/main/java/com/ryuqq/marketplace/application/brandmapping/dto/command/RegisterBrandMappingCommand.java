package com.ryuqq.marketplace.application.brandmapping.dto.command;

/** 브랜드 매핑 등록 커맨드 DTO. */
public record RegisterBrandMappingCommand(Long salesChannelBrandId, Long internalBrandId) {}
