package com.ryuqq.marketplace.domain.brandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** BrandMapping 검색 필드. */
public enum BrandMappingSearchField implements SearchField {

    /** 외부 채널 브랜드명 */
    EXTERNAL_BRAND_NAME("externalBrandName"),

    /** 내부 브랜드명 */
    INTERNAL_BRAND_NAME("internalBrandName");

    private final String fieldName;

    BrandMappingSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static BrandMappingSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (BrandMappingSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
