package com.ryuqq.marketplace.domain.seller.vo;

/** 통신판매업 신고번호 Value Object. nullable. */
public record SaleReportNumber(String value) {

    private static final int MAX_LENGTH = 50;

    public SaleReportNumber {
        if (value != null) {
            value = value.trim();
            if (value.isBlank()) {
                value = null;
            } else if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("통신판매업 신고번호는 " + MAX_LENGTH + "자를 초과할 수 없습니다");
            }
        }
    }

    public static SaleReportNumber of(String value) {
        return new SaleReportNumber(value);
    }

    public static SaleReportNumber empty() {
        return new SaleReportNumber(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
