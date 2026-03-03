package com.ryuqq.marketplace.domain.outboundproduct.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * OMS 상품 검색 필드.
 *
 * <p>OMS 상품 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum OmsProductSearchField implements SearchField {

    /** 상품 코드 (PG-{id}) */
    PRODUCT_CODE("productCode"),

    /** 상품명 */
    PRODUCT_NAME("productName"),

    /** 파트너(셀러)명 */
    PARTNER_NAME("partnerName");

    private final String fieldName;

    OmsProductSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 OmsProductSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return OmsProductSearchField (null이면 null 반환)
     */
    public static OmsProductSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (OmsProductSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
