package com.ryuqq.marketplace.application.channeloptionmapping.service.command;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.RegisterChannelOptionMappingCommand;
import com.ryuqq.marketplace.application.channeloptionmapping.factory.ChannelOptionMappingCommandFactory;
import com.ryuqq.marketplace.application.channeloptionmapping.manager.ChannelOptionMappingCommandManager;
import com.ryuqq.marketplace.application.channeloptionmapping.port.in.command.RegisterChannelOptionMappingUseCase;
import com.ryuqq.marketplace.application.channeloptionmapping.validator.ChannelOptionMappingValidator;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Service;

/** RegisterChannelOptionMappingService - 채널 옵션 매핑 등록 Service. */
@Service
public class RegisterChannelOptionMappingService implements RegisterChannelOptionMappingUseCase {

    private final ChannelOptionMappingCommandFactory commandFactory;
    private final ChannelOptionMappingValidator validator;
    private final ChannelOptionMappingCommandManager commandManager;

    public RegisterChannelOptionMappingService(
            ChannelOptionMappingCommandFactory commandFactory,
            ChannelOptionMappingValidator validator,
            ChannelOptionMappingCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.validator = validator;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterChannelOptionMappingCommand command) {
        validator.validateNotDuplicate(
                SalesChannelId.of(command.salesChannelId()),
                CanonicalOptionValueId.of(command.canonicalOptionValueId()));

        ChannelOptionMapping mapping = commandFactory.createChannelOptionMapping(command);
        return commandManager.persist(mapping);
    }
}
