package com.ryuqq.marketplace.application.externalcategorymapping.dto.command;

import java.util.List;

/** 외부 카테고리 매핑 일괄 등록 Command. */
public record BatchRegisterExternalCategoryMappingCommand(
        long externalSourceId, List<MappingEntry> entries) {

    /** 개별 매핑 엔트리. */
    public record MappingEntry(
            String externalCategoryCode, String externalCategoryName, long internalCategoryId) {}
}
