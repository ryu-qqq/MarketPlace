package com.ryuqq.marketplace.domain.settlement.entry.exception;

/** 정산 원장 미존재 예외. */
public class SettlementEntryNotFoundException extends SettlementEntryException {

    public SettlementEntryNotFoundException(String entryId) {
        super(
                SettlementEntryErrorCode.ENTRY_NOT_FOUND,
                String.format("정산 원장을 찾을 수 없습니다: %s", entryId));
    }
}
