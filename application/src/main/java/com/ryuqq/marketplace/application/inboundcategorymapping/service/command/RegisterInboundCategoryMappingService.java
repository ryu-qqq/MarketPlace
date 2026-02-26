package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.RegisterInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 등록 Service. */
@Service
public class RegisterInboundCategoryMappingService
        implements RegisterInboundCategoryMappingUseCase {

    private final InboundCategoryMappingValidator validator;
    private final InboundCategoryMappingCommandFactory commandFactory;
    private final InboundCategoryMappingCommandManager commandManager;

    public RegisterInboundCategoryMappingService(
            InboundCategoryMappingValidator validator,
            InboundCategoryMappingCommandFactory commandFactory,
            InboundCategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterInboundCategoryMappingCommand command) {
        validator.validateNotDuplicate(command.inboundSourceId(), command.externalCategoryCode());
        InboundCategoryMapping mapping = commandFactory.create(command);
        return commandManager.persist(mapping);
    }
}
