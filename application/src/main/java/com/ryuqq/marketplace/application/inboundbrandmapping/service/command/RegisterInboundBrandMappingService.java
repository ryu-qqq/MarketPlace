package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.RegisterInboundBrandMappingUseCase;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 등록 Service. */
@Service
public class RegisterInboundBrandMappingService implements RegisterInboundBrandMappingUseCase {

    private final InboundBrandMappingValidator validator;
    private final InboundBrandMappingCommandFactory commandFactory;
    private final InboundBrandMappingCommandManager commandManager;

    public RegisterInboundBrandMappingService(
            InboundBrandMappingValidator validator,
            InboundBrandMappingCommandFactory commandFactory,
            InboundBrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterInboundBrandMappingCommand command) {
        validator.validateNotDuplicate(command.inboundSourceId(), command.externalBrandCode());
        InboundBrandMapping mapping = commandFactory.create(command);
        return commandManager.persist(mapping);
    }
}
