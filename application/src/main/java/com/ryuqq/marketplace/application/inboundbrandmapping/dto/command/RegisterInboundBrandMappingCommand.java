package com.ryuqq.marketplace.application.inboundbrandmapping.dto.command;

/** 외부 브랜드 매핑 등록 Command. */
public record RegisterInboundBrandMappingCommand(
        long inboundSourceId,
        String externalBrandCode,
        String externalBrandName,
        long internalBrandId) {}
