package com.ryuqq.marketplace.domain.claimsync.vo;

/** 외부몰 클레임 유형. */
public enum ExternalClaimType {
    CANCEL,
    RETURN,
    EXCHANGE,
    ADMIN_CANCEL;

    /**
     * 외부 클레임 유형을 내부 클레임 유형으로 변환합니다.
     *
     * @return 대응하는 InternalClaimType
     */
    public InternalClaimType toInternalType() {
        return switch (this) {
            case CANCEL, ADMIN_CANCEL -> InternalClaimType.CANCEL;
            case RETURN -> InternalClaimType.REFUND;
            case EXCHANGE -> InternalClaimType.EXCHANGE;
        };
    }

    /**
     * 판매자(운영자) 취소 여부를 반환합니다.
     *
     * @return ADMIN_CANCEL이면 true
     */
    public boolean isAdminCancel() {
        return this == ADMIN_CANCEL;
    }

    /**
     * 문자열로부터 ExternalClaimType을 파싱합니다.
     *
     * @param value 클레임 유형 문자열
     * @return 대응하는 ExternalClaimType
     * @throws IllegalArgumentException value가 null이거나 빈 문자열이거나 알 수 없는 값인 경우
     */
    public static ExternalClaimType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExternalClaimType 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return valueOf(value);
    }
}
