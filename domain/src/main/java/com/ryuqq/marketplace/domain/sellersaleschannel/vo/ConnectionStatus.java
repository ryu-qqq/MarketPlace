package com.ryuqq.marketplace.domain.sellersaleschannel.vo;

/**
 * 셀러 판매채널 연동 상태.
 *
 * <p>셀러와 외부 판매채널 간 연동 상태를 나타냅니다.
 */
@SuppressWarnings("PMD.DataClass")
public enum ConnectionStatus {

    /** 연동 완료. 정상적으로 상품 연동 가능. */
    CONNECTED("연동"),

    /** 연동 해제. 상품 연동 불가. */
    DISCONNECTED("해제"),

    /** 연동 일시 중지. 관리자에 의해 임시 차단. */
    SUSPENDED("중지");

    private final String description;

    ConnectionStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isConnected() {
        return this == CONNECTED;
    }

    public boolean isDisconnected() {
        return this == DISCONNECTED;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }
}
