package com.ryuqq.marketplace.domain.externalbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** ExternalBrandMapping 검색 필드. */
public enum ExternalBrandMappingSearchField implements SearchField {

    /** 외부 브랜드 코드 */
    EXTERNAL_CODE("externalCode"),

    /** 외부 브랜드 이름 */
    EXTERNAL_NAME("externalName");

    private final String fieldName;

    ExternalBrandMappingSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 ExternalBrandMappingSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return ExternalBrandMappingSearchField (null이면 null 반환)
     */
    public static ExternalBrandMappingSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ExternalBrandMappingSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
