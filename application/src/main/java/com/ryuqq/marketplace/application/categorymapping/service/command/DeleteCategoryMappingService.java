package com.ryuqq.marketplace.application.categorymapping.service.command;

import com.ryuqq.marketplace.application.categorymapping.dto.command.DeleteCategoryMappingCommand;
import com.ryuqq.marketplace.application.categorymapping.factory.CategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingCommandManager;
import com.ryuqq.marketplace.application.categorymapping.port.in.command.DeleteCategoryMappingUseCase;
import com.ryuqq.marketplace.application.categorymapping.validator.CategoryMappingValidator;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import org.springframework.stereotype.Service;

/** 카테고리 매핑 비활성화 Service. */
@Service
public class DeleteCategoryMappingService implements DeleteCategoryMappingUseCase {

    private final CategoryMappingValidator validator;
    private final CategoryMappingCommandFactory commandFactory;
    private final CategoryMappingCommandManager commandManager;

    public DeleteCategoryMappingService(
            CategoryMappingValidator validator,
            CategoryMappingCommandFactory commandFactory,
            CategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(DeleteCategoryMappingCommand command) {
        StatusChangeContext<CategoryMappingId> context =
                commandFactory.createDeactivateContext(command);
        CategoryMapping categoryMapping = validator.findExistingOrThrow(context.id());
        categoryMapping.deactivate(context.changedAt());
        commandManager.persist(categoryMapping);
    }
}
