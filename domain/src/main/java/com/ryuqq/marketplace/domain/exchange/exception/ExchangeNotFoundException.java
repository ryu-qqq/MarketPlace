package com.ryuqq.marketplace.domain.exchange.exception;

/** 교환 클레임을 찾을 수 없는 경우 예외. */
public class ExchangeNotFoundException extends ExchangeException {

    private static final ExchangeErrorCode ERROR_CODE = ExchangeErrorCode.EXCHANGE_NOT_FOUND;

    public ExchangeNotFoundException() {
        super(ERROR_CODE);
    }

    public ExchangeNotFoundException(String exchangeClaimId) {
        super(ERROR_CODE, String.format("ID가 %s인 교환 클레임을 찾을 수 없습니다", exchangeClaimId));
    }
}
