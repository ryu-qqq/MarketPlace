package com.ryuqq.marketplace.domain.claimsync.vo;

/** 클레임 동기화 단건 처리 결과. */
public enum ClaimSyncOutcome {
    CANCEL_SYNCED,
    REFUND_SYNCED,
    EXCHANGE_SYNCED,
    SKIPPED;

    /**
     * InternalClaimType으로부터 ClaimSyncOutcome을 결정합니다.
     *
     * @param type 내부 클레임 유형
     * @return 대응하는 ClaimSyncOutcome
     */
    public static ClaimSyncOutcome fromInternalType(InternalClaimType type) {
        return switch (type) {
            case CANCEL -> CANCEL_SYNCED;
            case REFUND -> REFUND_SYNCED;
            case EXCHANGE -> EXCHANGE_SYNCED;
        };
    }
}
