package com.ryuqq.marketplace.domain.inboundorder.vo;

/** InboundOrder 상태. */
public enum InboundOrderStatus {
    RECEIVED,
    MAPPED,
    PENDING_MAPPING,
    CONVERTED,
    FAILED;

    public boolean canApplyMapping() {
        return this == RECEIVED || this == PENDING_MAPPING;
    }

    public boolean canConvert() {
        return this == MAPPED;
    }

    public boolean isTerminal() {
        return this == CONVERTED || this == FAILED;
    }
}
