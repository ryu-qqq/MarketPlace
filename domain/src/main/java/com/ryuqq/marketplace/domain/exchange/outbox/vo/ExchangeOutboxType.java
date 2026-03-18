package com.ryuqq.marketplace.domain.exchange.outbox.vo;

/**
 * 교환 아웃박스 유형.
 *
 * <p>외부 채널에 동기화해야 하는 교환 상태 변경 유형입니다.
 */
public enum ExchangeOutboxType {

    /** 수거 완료 (COLLECTING → COLLECTED) → 네이버 approveCollectedExchange() */
    COLLECT("수거 완료"),

    /** 재배송 (PREPARING → SHIPPING) → 네이버 reDeliverExchange() */
    SHIP("재배송"),

    /** 교환 거절 → 네이버 rejectExchange() */
    REJECT("교환 거절"),

    /** 교환 보류 → 네이버 holdbackExchange() */
    HOLD("교환 보류"),

    /** 교환 보류 해제 → 네이버 releaseExchangeHoldback() */
    RELEASE_HOLD("교환 보류 해제");

    private final String description;

    ExchangeOutboxType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
