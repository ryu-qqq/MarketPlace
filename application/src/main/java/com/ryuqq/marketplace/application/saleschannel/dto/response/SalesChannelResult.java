package com.ryuqq.marketplace.application.saleschannel.dto.response;

import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import java.time.Instant;

/** 판매채널 조회 결과 DTO. */
public record SalesChannelResult(
        Long id, String channelName, String status, Instant createdAt, Instant updatedAt) {

    public static SalesChannelResult from(SalesChannel salesChannel) {
        return new SalesChannelResult(
                salesChannel.idValue(),
                salesChannel.channelName(),
                salesChannel.status().name(),
                salesChannel.createdAt(),
                salesChannel.updatedAt());
    }
}
