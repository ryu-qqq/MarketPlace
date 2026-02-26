package com.ryuqq.marketplace.application.channeloptionmapping.factory;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.RegisterChannelOptionMappingCommand;
import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.UpdateChannelOptionMappingCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Component;

/**
 * ChannelOptionMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ChannelOptionMappingCommandFactory {

    private final TimeProvider timeProvider;

    public ChannelOptionMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 등록 Command → Domain Aggregate 변환. */
    public ChannelOptionMapping createChannelOptionMapping(
            RegisterChannelOptionMappingCommand command) {
        return ChannelOptionMapping.forNew(
                SalesChannelId.of(command.salesChannelId()),
                CanonicalOptionValueId.of(command.canonicalOptionValueId()),
                ExternalOptionCode.of(command.externalOptionCode()),
                timeProvider.now());
    }

    /** 수정 Command → StatusChangeContext 변환. */
    public StatusChangeContext<ChannelOptionMappingId> createUpdateContext(
            UpdateChannelOptionMappingCommand command) {
        return new StatusChangeContext<>(
                ChannelOptionMappingId.of(command.channelOptionMappingId()), timeProvider.now());
    }
}
