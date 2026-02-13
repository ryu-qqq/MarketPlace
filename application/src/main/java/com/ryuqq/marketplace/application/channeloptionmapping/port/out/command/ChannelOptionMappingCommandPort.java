package com.ryuqq.marketplace.application.channeloptionmapping.port.out.command;

import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;

/** ChannelOptionMapping Command Port. */
public interface ChannelOptionMappingCommandPort {

    Long persist(ChannelOptionMapping channelOptionMapping);
}
