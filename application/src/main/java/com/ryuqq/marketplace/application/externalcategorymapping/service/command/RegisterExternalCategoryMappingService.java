package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.RegisterExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.application.externalcategorymapping.validator.ExternalCategoryMappingValidator;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 등록 Service. */
@Service
public class RegisterExternalCategoryMappingService
        implements RegisterExternalCategoryMappingUseCase {

    private final ExternalCategoryMappingValidator validator;
    private final ExternalCategoryMappingCommandFactory commandFactory;
    private final ExternalCategoryMappingCommandManager commandManager;

    public RegisterExternalCategoryMappingService(
            ExternalCategoryMappingValidator validator,
            ExternalCategoryMappingCommandFactory commandFactory,
            ExternalCategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterExternalCategoryMappingCommand command) {
        validator.validateNotDuplicate(command.externalSourceId(), command.externalCategoryCode());
        ExternalCategoryMapping mapping = commandFactory.create(command);
        return commandManager.persist(mapping);
    }
}
