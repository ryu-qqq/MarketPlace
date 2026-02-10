package com.ryuqq.marketplace.domain.saleschannel.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 판매채널 정렬 키. */
public enum SalesChannelSortKey implements SortKey {
    CREATED_AT("createdAt"),
    CHANNEL_NAME("channelName");

    private final String fieldName;

    SalesChannelSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SalesChannelSortKey defaultKey() {
        return CREATED_AT;
    }
}
