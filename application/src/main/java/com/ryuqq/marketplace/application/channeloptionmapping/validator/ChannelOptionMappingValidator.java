package com.ryuqq.marketplace.application.channeloptionmapping.validator;

import com.ryuqq.marketplace.application.channeloptionmapping.manager.ChannelOptionMappingReadManager;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.exception.ChannelOptionMappingDuplicateException;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping 비즈니스 검증. */
@Component
public class ChannelOptionMappingValidator {

    private final ChannelOptionMappingReadManager readManager;

    public ChannelOptionMappingValidator(ChannelOptionMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /** salesChannelId + canonicalOptionValueId 중복 검증. */
    public void validateNotDuplicate(
            SalesChannelId salesChannelId, CanonicalOptionValueId canonicalOptionValueId) {
        if (readManager.existsBySalesChannelIdAndCanonicalOptionValueId(
                salesChannelId, canonicalOptionValueId)) {
            throw new ChannelOptionMappingDuplicateException(
                    salesChannelId.value(), canonicalOptionValueId.value());
        }
    }
}
