package com.ryuqq.marketplace.domain.commoncodetype.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * CommonCodeType 검색 필드.
 *
 * <p>공통 코드 타입 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum CommonCodeTypeSearchField implements SearchField {

    /** 코드 */
    CODE("code"),

    /** 이름 */
    NAME("name");

    private final String fieldName;

    CommonCodeTypeSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 CommonCodeTypeSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return CommonCodeTypeSearchField (null이면 null 반환)
     */
    public static CommonCodeTypeSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (CommonCodeTypeSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
