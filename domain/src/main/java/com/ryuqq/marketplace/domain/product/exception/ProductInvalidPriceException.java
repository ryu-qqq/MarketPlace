package com.ryuqq.marketplace.domain.product.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 가격 체계가 유효하지 않을 때 발생하는 예외. (salePrice <= currentPrice <= regularPrice 위반) */
public class ProductInvalidPriceException extends DomainException {

    public ProductInvalidPriceException(int regularPrice, int currentPrice, int salePrice) {
        super(
                ProductErrorCode.PRODUCT_INVALID_PRICE,
                String.format(
                        "가격 체계가 유효하지 않습니다: regularPrice=%d, currentPrice=%d, salePrice=%d",
                        regularPrice, currentPrice, salePrice),
                Map.of(
                        "regularPrice",
                        regularPrice,
                        "currentPrice",
                        currentPrice,
                        "salePrice",
                        salePrice));
    }
}
