package com.ryuqq.marketplace.domain.inboundproduct.vo;

/** 외부 상품 코드 Value Object. 세토프 PK 또는 크롤링 키를 나타내는 불변 VO. */
public record ExternalProductCode(String value) {

    public ExternalProductCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExternalProductCode 값은 null이거나 비어있을 수 없습니다");
        }
    }

    public static ExternalProductCode of(String value) {
        return new ExternalProductCode(value);
    }
}
