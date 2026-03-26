package com.ryuqq.marketplace.domain.exchange.exception;

import java.util.List;

/** 교환 건의 소유권이 일치하지 않는 경우 예외. */
public class ExchangeOwnershipMismatchException extends ExchangeException {

    private static final ExchangeErrorCode ERROR_CODE =
            ExchangeErrorCode.EXCHANGE_OWNERSHIP_MISMATCH;

    public ExchangeOwnershipMismatchException() {
        super(ERROR_CODE);
    }

    public ExchangeOwnershipMismatchException(List<String> missingIds) {
        super(ERROR_CODE, String.format("소유권 불일치 또는 존재하지 않는 교환 건: %s", missingIds));
    }
}
