package com.ryuqq.marketplace.domain.selleraddress.vo;

/** 주소 별칭 Value Object. nullable. 예: "본사 창고", "물류센터" */
public record AddressName(String value) {

    private static final int MAX_LENGTH = 50;

    public AddressName {
        if (value != null) {
            value = value.trim();
            if (value.isBlank()) {
                value = null;
            } else if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("주소 별칭은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
            }
        }
    }

    public static AddressName of(String value) {
        return new AddressName(value);
    }

    public static AddressName empty() {
        return new AddressName(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
