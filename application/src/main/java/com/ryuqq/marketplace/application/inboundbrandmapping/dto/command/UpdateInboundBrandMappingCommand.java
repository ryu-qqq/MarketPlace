package com.ryuqq.marketplace.application.inboundbrandmapping.dto.command;

/** 외부 브랜드 매핑 수정 Command. */
public record UpdateInboundBrandMappingCommand(
        long id, String externalBrandName, long internalBrandId, String status) {}
