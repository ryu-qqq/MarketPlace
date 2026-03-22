package com.ryuqq.marketplace.domain.inboundqna.vo;

public enum InboundQnaStatus {
    RECEIVED,
    CONVERTED,
    FAILED;

    public boolean canConvert() {
        return this == RECEIVED;
    }

    public boolean isTerminal() {
        return this == CONVERTED || this == FAILED;
    }
}
