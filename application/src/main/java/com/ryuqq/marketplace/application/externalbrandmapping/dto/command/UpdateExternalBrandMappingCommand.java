package com.ryuqq.marketplace.application.externalbrandmapping.dto.command;

/** 외부 브랜드 매핑 수정 Command. */
public record UpdateExternalBrandMappingCommand(
        long id, String externalBrandName, long internalBrandId, String status) {}
