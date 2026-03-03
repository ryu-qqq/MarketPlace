package com.ryuqq.marketplace.domain.productgroup.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * ProductGroup 검색 필드.
 *
 * <p>상품 그룹 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum ProductGroupSearchField implements SearchField {

    /** 상품 그룹 ID */
    PRODUCT_GROUP_ID("productGroupId"),

    /** 상품 그룹명 */
    PRODUCT_GROUP_NAME("productGroupName");

    private final String fieldName;

    ProductGroupSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 ProductGroupSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return ProductGroupSearchField (null이면 null 반환)
     */
    public static ProductGroupSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ProductGroupSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
