package com.ryuqq.marketplace.domain.selleraddress.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * SellerAddress 검색 필드.
 *
 * <p>셀러 주소 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum SellerAddressSearchField implements SearchField {

    /** 주소별칭 */
    ADDRESS_NAME("addressName"),

    /** 주소 */
    ADDRESS("address");

    private final String fieldName;

    SellerAddressSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SellerAddressSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SellerAddressSearchField (null이면 null 반환)
     */
    public static SellerAddressSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SellerAddressSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
