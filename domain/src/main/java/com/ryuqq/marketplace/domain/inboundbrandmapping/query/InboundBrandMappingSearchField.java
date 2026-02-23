package com.ryuqq.marketplace.domain.inboundbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** InboundBrandMapping 검색 필드. */
public enum InboundBrandMappingSearchField implements SearchField {

    /** 외부 브랜드 코드 */
    EXTERNAL_CODE("externalCode"),

    /** 외부 브랜드 이름 */
    EXTERNAL_NAME("externalName");

    private final String fieldName;

    InboundBrandMappingSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 InboundBrandMappingSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return InboundBrandMappingSearchField (null이면 null 반환)
     */
    public static InboundBrandMappingSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (InboundBrandMappingSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
