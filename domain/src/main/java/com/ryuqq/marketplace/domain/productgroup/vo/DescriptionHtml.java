package com.ryuqq.marketplace.domain.productgroup.vo;

/**
 * 상품 상세설명 HTML Value Object.
 * Persistence 레이어에서 별도 테이블(product_group_description)로 분리 저장.
 */
public record DescriptionHtml(String value) {

    public DescriptionHtml {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                value = null;
            }
        }
    }

    public static DescriptionHtml of(String value) {
        return new DescriptionHtml(value);
    }

    public static DescriptionHtml empty() {
        return new DescriptionHtml(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
