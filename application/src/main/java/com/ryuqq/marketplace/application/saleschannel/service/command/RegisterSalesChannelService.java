package com.ryuqq.marketplace.application.saleschannel.service.command;

import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelCommandFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelCommandManager;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.RegisterSalesChannelUseCase;
import com.ryuqq.marketplace.application.saleschannel.validator.SalesChannelValidator;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import org.springframework.stereotype.Service;

/** 판매채널 등록 Service. */
@Service
public class RegisterSalesChannelService implements RegisterSalesChannelUseCase {

    private final SalesChannelValidator validator;
    private final SalesChannelCommandFactory commandFactory;
    private final SalesChannelCommandManager commandManager;

    public RegisterSalesChannelService(
            SalesChannelValidator validator,
            SalesChannelCommandFactory commandFactory,
            SalesChannelCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterSalesChannelCommand command) {
        validator.validateChannelNameNotDuplicate(command.channelName());

        SalesChannel salesChannel = commandFactory.create(command);
        return commandManager.persist(salesChannel);
    }
}
