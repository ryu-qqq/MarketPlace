package com.ryuqq.marketplace.domain.outboundproduct.vo;

/**
 * OutboundProduct 상태 enum.
 *
 * <p>상태 머신:
 *
 * <pre>
 * PENDING_REGISTRATION → REGISTERED              (외부 채널에 등록 성공)
 * PENDING_REGISTRATION → REGISTRATION_FAILED     (등록 실패)
 * REGISTRATION_FAILED → PENDING_REGISTRATION     (재시도)
 * REGISTERED → DEREGISTERED                      (외부 채널 삭제 성공)
 * DEREGISTERED → PENDING_REGISTRATION            (재활성화 시 재등록 대기)
 * </pre>
 */
public enum OutboundProductStatus {
    PENDING_REGISTRATION("등록 대기"),
    REGISTERED("등록 완료"),
    REGISTRATION_FAILED("등록 실패"),
    DEREGISTERED("등록 해제");

    private final String description;

    OutboundProductStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPendingRegistration() {
        return this == PENDING_REGISTRATION;
    }

    public boolean isRegistered() {
        return this == REGISTERED;
    }

    public boolean isRegistrationFailed() {
        return this == REGISTRATION_FAILED;
    }

    public boolean isDeregistered() {
        return this == DEREGISTERED;
    }

    public static OutboundProductStatus fromString(String status) {
        return OutboundProductStatus.valueOf(status);
    }
}
