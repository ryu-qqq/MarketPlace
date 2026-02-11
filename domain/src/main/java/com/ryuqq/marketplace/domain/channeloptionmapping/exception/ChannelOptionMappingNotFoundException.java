package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 채널 옵션 매핑을 찾을 수 없을 때 발생하는 예외. */
public class ChannelOptionMappingNotFoundException extends DomainException {

    public ChannelOptionMappingNotFoundException(Long channelOptionMappingId) {
        super(
                ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND,
                String.format("채널 옵션 매핑을 찾을 수 없습니다: %d", channelOptionMappingId),
                Map.of("channelOptionMappingId", channelOptionMappingId));
    }
}
