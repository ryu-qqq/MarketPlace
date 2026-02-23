package com.ryuqq.marketplace.domain.inboundcategorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** InboundCategoryMapping 검색 필드. */
public enum InboundCategoryMappingSearchField implements SearchField {

    /** 외부 카테고리 코드 */
    EXTERNAL_CODE("externalCode"),

    /** 외부 카테고리 이름 */
    EXTERNAL_NAME("externalName");

    private final String fieldName;

    InboundCategoryMappingSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 InboundCategoryMappingSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return InboundCategoryMappingSearchField (null이면 null 반환)
     */
    public static InboundCategoryMappingSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (InboundCategoryMappingSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
