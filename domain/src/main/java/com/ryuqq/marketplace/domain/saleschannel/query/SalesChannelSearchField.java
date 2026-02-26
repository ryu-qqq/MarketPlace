package com.ryuqq.marketplace.domain.saleschannel.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * SalesChannel 검색 필드.
 *
 * <p>판매채널 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum SalesChannelSearchField implements SearchField {

    /** 판매채널명 */
    CHANNEL_NAME("channelName");

    private final String fieldName;

    SalesChannelSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SalesChannelSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SalesChannelSearchField (null이면 null 반환)
     */
    public static SalesChannelSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SalesChannelSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
