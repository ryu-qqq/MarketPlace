package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * SalesChannelCategory 검색 필드.
 *
 * <p>외부채널 카테고리 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum SalesChannelCategorySearchField implements SearchField {

    /** 외부 카테고리 코드 */
    EXTERNAL_CODE("externalCategoryCode"),

    /** 외부 카테고리명 */
    EXTERNAL_NAME("externalCategoryName");

    private final String fieldName;

    SalesChannelCategorySearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SalesChannelCategorySearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SalesChannelCategorySearchField (null이면 null 반환)
     */
    public static SalesChannelCategorySearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SalesChannelCategorySearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
