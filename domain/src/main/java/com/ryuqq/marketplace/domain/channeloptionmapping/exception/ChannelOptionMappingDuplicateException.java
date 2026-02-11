package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import java.util.Map;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 중복된 채널 옵션 매핑이 이미 존재할 때 발생하는 예외. */
public class ChannelOptionMappingDuplicateException extends DomainException {

    public ChannelOptionMappingDuplicateException(Long salesChannelId, Long canonicalOptionValueId) {
        super(
                ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE,
                String.format("salesChannelId=%d, canonicalOptionValueId=%d 조합의 매핑이 이미 존재합니다",
                        salesChannelId, canonicalOptionValueId),
                Map.of("salesChannelId", salesChannelId,
                        "canonicalOptionValueId", canonicalOptionValueId));
    }
}
