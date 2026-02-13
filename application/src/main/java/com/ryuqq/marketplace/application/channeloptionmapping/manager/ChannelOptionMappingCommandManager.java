package com.ryuqq.marketplace.application.channeloptionmapping.manager;

import com.ryuqq.marketplace.application.channeloptionmapping.port.out.command.ChannelOptionMappingCommandPort;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ChannelOptionMapping Command Manager. */
@Component
public class ChannelOptionMappingCommandManager {

    private final ChannelOptionMappingCommandPort commandPort;

    public ChannelOptionMappingCommandManager(ChannelOptionMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ChannelOptionMapping channelOptionMapping) {
        return commandPort.persist(channelOptionMapping);
    }
}
