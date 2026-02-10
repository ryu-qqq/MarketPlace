package com.ryuqq.marketplace.domain.saleschannel.aggregate;

import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;

/** 판매채널 수정 데이터. */
public record SalesChannelUpdateData(String channelName, SalesChannelStatus status) {

    public static SalesChannelUpdateData of(String channelName, SalesChannelStatus status) {
        return new SalesChannelUpdateData(channelName, status);
    }
}
