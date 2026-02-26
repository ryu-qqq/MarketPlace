package com.ryuqq.marketplace.domain.inboundsource.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** InboundSource 검색 필드. */
public enum InboundSourceSearchField implements SearchField {

    /** 코드 */
    CODE("code"),

    /** 이름 */
    NAME("name");

    private final String fieldName;

    InboundSourceSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 InboundSourceSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return InboundSourceSearchField (null이면 null 반환)
     */
    public static InboundSourceSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (InboundSourceSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
