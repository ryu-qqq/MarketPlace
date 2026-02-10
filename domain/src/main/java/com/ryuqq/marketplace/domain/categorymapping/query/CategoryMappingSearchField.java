package com.ryuqq.marketplace.domain.categorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** CategoryMapping 검색 필드. */
public enum CategoryMappingSearchField implements SearchField {

    /** 외부 채널 카테고리명 */
    EXTERNAL_CATEGORY_NAME("externalCategoryName"),

    /** 내부 카테고리명 */
    INTERNAL_CATEGORY_NAME("internalCategoryName");

    private final String fieldName;

    CategoryMappingSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CategoryMappingSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (CategoryMappingSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
