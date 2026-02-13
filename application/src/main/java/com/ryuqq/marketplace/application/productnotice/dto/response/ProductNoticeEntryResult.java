package com.ryuqq.marketplace.application.productnotice.dto.response;

import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;

/** 고시정보 항목 조회 결과 DTO. */
public record ProductNoticeEntryResult(Long id, Long noticeFieldId, String fieldValue) {

    public static ProductNoticeEntryResult from(ProductNoticeEntry entry) {
        return new ProductNoticeEntryResult(
                entry.idValue(), entry.noticeFieldIdValue(), entry.fieldValueValue());
    }
}
