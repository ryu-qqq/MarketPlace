package com.ryuqq.marketplace.application.channeloptionmapping.service.command;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.UpdateChannelOptionMappingCommand;
import com.ryuqq.marketplace.application.channeloptionmapping.factory.ChannelOptionMappingCommandFactory;
import com.ryuqq.marketplace.application.channeloptionmapping.manager.ChannelOptionMappingCommandManager;
import com.ryuqq.marketplace.application.channeloptionmapping.manager.ChannelOptionMappingReadManager;
import com.ryuqq.marketplace.application.channeloptionmapping.port.in.command.UpdateChannelOptionMappingUseCase;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import org.springframework.stereotype.Service;

/**
 * UpdateChannelOptionMappingService - 채널 옵션 매핑 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class UpdateChannelOptionMappingService implements UpdateChannelOptionMappingUseCase {

    private final ChannelOptionMappingCommandFactory commandFactory;
    private final ChannelOptionMappingReadManager readManager;
    private final ChannelOptionMappingCommandManager commandManager;

    public UpdateChannelOptionMappingService(
            ChannelOptionMappingCommandFactory commandFactory,
            ChannelOptionMappingReadManager readManager,
            ChannelOptionMappingCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateChannelOptionMappingCommand command) {
        StatusChangeContext<ChannelOptionMappingId> context =
                commandFactory.createUpdateContext(command);

        ChannelOptionMapping mapping = readManager.getById(context.id());
        mapping.updateExternalOptionCode(
                ExternalOptionCode.of(command.externalOptionCode()), context.changedAt());

        commandManager.persist(mapping);
    }
}
