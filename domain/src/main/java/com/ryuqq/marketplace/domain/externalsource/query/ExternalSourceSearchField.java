package com.ryuqq.marketplace.domain.externalsource.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** ExternalSource 검색 필드. */
public enum ExternalSourceSearchField implements SearchField {

    /** 코드 */
    CODE("code"),

    /** 이름 */
    NAME("name");

    private final String fieldName;

    ExternalSourceSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 ExternalSourceSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return ExternalSourceSearchField (null이면 null 반환)
     */
    public static ExternalSourceSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ExternalSourceSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
