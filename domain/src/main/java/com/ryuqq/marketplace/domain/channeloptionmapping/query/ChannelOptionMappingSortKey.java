package com.ryuqq.marketplace.domain.channeloptionmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * ChannelOptionMapping 정렬 키.
 *
 * <p>채널 옵션 매핑 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum ChannelOptionMappingSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 수정일시 순 */
    UPDATED_AT("updatedAt");

    private final String fieldName;

    ChannelOptionMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ChannelOptionMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
