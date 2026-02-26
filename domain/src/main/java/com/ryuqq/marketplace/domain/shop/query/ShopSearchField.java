package com.ryuqq.marketplace.domain.shop.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * Shop 검색 필드.
 *
 * <p>외부몰 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum ShopSearchField implements SearchField {

    /** 외부몰명 */
    SHOP_NAME("shopName"),

    /** 계정 ID */
    ACCOUNT_ID("accountId");

    private final String fieldName;

    ShopSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 ShopSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return ShopSearchField (null이면 null 반환)
     */
    public static ShopSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ShopSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
