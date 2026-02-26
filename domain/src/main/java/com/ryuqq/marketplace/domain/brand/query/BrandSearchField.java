package com.ryuqq.marketplace.domain.brand.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * Brand 검색 필드.
 *
 * <p>브랜드 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum BrandSearchField implements SearchField {

    /** 브랜드 코드 */
    CODE("code"),

    /** 한글명 */
    NAME_KO("nameKo"),

    /** 영문명 */
    NAME_EN("nameEn");

    private final String fieldName;

    BrandSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 BrandSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return BrandSearchField (null이면 null 반환)
     */
    public static BrandSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (BrandSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
