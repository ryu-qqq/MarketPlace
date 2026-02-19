package com.ryuqq.marketplace.application.externalbrandmapping.dto.command;

/** 외부 브랜드 매핑 등록 Command. */
public record RegisterExternalBrandMappingCommand(
        long externalSourceId,
        String externalBrandCode,
        String externalBrandName,
        long internalBrandId) {}
