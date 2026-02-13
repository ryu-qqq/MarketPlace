package com.ryuqq.marketplace.domain.exchange.vo;

/** 교환 사유. */
public record ExchangeReason(ExchangeReasonType reasonType, String reasonDetail) {

    public ExchangeReason {
        if (reasonType == null) {
            throw new IllegalArgumentException("교환 사유 유형은 null일 수 없습니다");
        }
        if (reasonDetail == null || reasonDetail.isBlank()) {
            throw new IllegalArgumentException("교환 사유 상세는 null 또는 빈 문자열일 수 없습니다");
        }
    }
}
