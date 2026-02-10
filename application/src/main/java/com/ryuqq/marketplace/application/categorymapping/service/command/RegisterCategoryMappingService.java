package com.ryuqq.marketplace.application.categorymapping.service.command;

import com.ryuqq.marketplace.application.categorymapping.dto.command.RegisterCategoryMappingCommand;
import com.ryuqq.marketplace.application.categorymapping.factory.CategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingCommandManager;
import com.ryuqq.marketplace.application.categorymapping.port.in.command.RegisterCategoryMappingUseCase;
import com.ryuqq.marketplace.application.categorymapping.validator.CategoryMappingValidator;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import org.springframework.stereotype.Service;

/** 카테고리 매핑 등록 Service. */
@Service
public class RegisterCategoryMappingService implements RegisterCategoryMappingUseCase {

    private final CategoryMappingValidator validator;
    private final CategoryMappingCommandFactory commandFactory;
    private final CategoryMappingCommandManager commandManager;

    public RegisterCategoryMappingService(
            CategoryMappingValidator validator,
            CategoryMappingCommandFactory commandFactory,
            CategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterCategoryMappingCommand command) {
        validator.validateNotDuplicate(command.salesChannelCategoryId());

        CategoryMapping categoryMapping = commandFactory.create(command);
        return commandManager.persist(categoryMapping);
    }
}
