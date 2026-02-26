package com.ryuqq.marketplace.application.inboundbrandmapping.dto.command;

import java.util.List;

/** 외부 브랜드 매핑 일괄 등록 Command. */
public record BatchRegisterInboundBrandMappingCommand(
        long inboundSourceId, List<MappingEntry> entries) {

    /** 개별 매핑 엔트리. */
    public record MappingEntry(
            String externalBrandCode, String externalBrandName, long internalBrandId) {}
}
