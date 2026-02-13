package com.ryuqq.marketplace.application.channeloptionmapping.dto.response;

import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import java.time.Instant;

/** 채널 옵션 매핑 조회 결과 DTO. */
public record ChannelOptionMappingResult(
        Long id,
        Long salesChannelId,
        Long canonicalOptionValueId,
        String externalOptionCode,
        Instant createdAt,
        Instant updatedAt) {

    public static ChannelOptionMappingResult from(ChannelOptionMapping mapping) {
        return new ChannelOptionMappingResult(
                mapping.idValue(),
                mapping.salesChannelIdValue(),
                mapping.canonicalOptionValueIdValue(),
                mapping.externalOptionCodeValue(),
                mapping.createdAt(),
                mapping.updatedAt());
    }
}
