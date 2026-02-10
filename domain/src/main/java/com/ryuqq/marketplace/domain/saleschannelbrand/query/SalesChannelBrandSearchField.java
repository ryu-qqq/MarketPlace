package com.ryuqq.marketplace.domain.saleschannelbrand.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * SalesChannelBrand 검색 필드.
 *
 * <p>외부채널 브랜드 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum SalesChannelBrandSearchField implements SearchField {

    /** 외부 브랜드 코드 */
    EXTERNAL_CODE("externalBrandCode"),

    /** 외부 브랜드명 */
    EXTERNAL_NAME("externalBrandName");

    private final String fieldName;

    SalesChannelBrandSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SalesChannelBrandSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SalesChannelBrandSearchField (null이면 null 반환)
     */
    public static SalesChannelBrandSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SalesChannelBrandSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
