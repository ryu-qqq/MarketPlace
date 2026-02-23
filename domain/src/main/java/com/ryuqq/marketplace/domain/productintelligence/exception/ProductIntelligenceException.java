package com.ryuqq.marketplace.domain.productintelligence.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** ProductIntelligence 도메인 예외. */
public class ProductIntelligenceException extends DomainException {

    public ProductIntelligenceException(ProductIntelligenceErrorCode errorCode) {
        super(errorCode);
    }

    public ProductIntelligenceException(
            ProductIntelligenceErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ProductIntelligenceException(ProductIntelligenceErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
