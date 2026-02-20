package com.ryuqq.marketplace.domain.product.vo;

/** SKU 코드 Value Object. nullable 허용 (NONE 옵션 타입은 SKU 없을 수 있음). */
public record SkuCode(String value) {

    public SkuCode {
        if (value != null && value.length() > 100) {
            throw new IllegalArgumentException("SKU 코드는 100자를 초과할 수 없습니다");
        }
    }

    public static SkuCode of(String value) {
        return new SkuCode(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
