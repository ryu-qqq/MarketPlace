package com.ryuqq.marketplace.domain.settlement.entry.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 정산 원장 예외. */
public class SettlementEntryException extends DomainException {

    public SettlementEntryException(SettlementEntryErrorCode errorCode) {
        super(errorCode);
    }

    public SettlementEntryException(SettlementEntryErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
