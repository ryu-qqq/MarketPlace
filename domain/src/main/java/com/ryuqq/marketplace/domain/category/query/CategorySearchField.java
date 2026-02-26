package com.ryuqq.marketplace.domain.category.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * Category 검색 필드.
 *
 * <p>카테고리 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum CategorySearchField implements SearchField {

    /** 카테고리 코드 */
    CODE("code"),

    /** 한글명 */
    NAME_KO("nameKo"),

    /** 영문명 */
    NAME_EN("nameEn");

    private final String fieldName;

    CategorySearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 CategorySearchField 변환.
     *
     * @param value 필드명 문자열
     * @return CategorySearchField (null이면 null 반환)
     */
    public static CategorySearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (CategorySearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
