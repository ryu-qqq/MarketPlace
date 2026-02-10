package com.ryuqq.marketplace.application.brandmapping.service.command;

import com.ryuqq.marketplace.application.brandmapping.dto.command.DeleteBrandMappingCommand;
import com.ryuqq.marketplace.application.brandmapping.factory.BrandMappingCommandFactory;
import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingCommandManager;
import com.ryuqq.marketplace.application.brandmapping.port.in.command.DeleteBrandMappingUseCase;
import com.ryuqq.marketplace.application.brandmapping.validator.BrandMappingValidator;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import org.springframework.stereotype.Service;

/** 브랜드 매핑 비활성화 Service. */
@Service
public class DeleteBrandMappingService implements DeleteBrandMappingUseCase {

    private final BrandMappingValidator validator;
    private final BrandMappingCommandFactory commandFactory;
    private final BrandMappingCommandManager commandManager;

    public DeleteBrandMappingService(
            BrandMappingValidator validator,
            BrandMappingCommandFactory commandFactory,
            BrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(DeleteBrandMappingCommand command) {
        StatusChangeContext<BrandMappingId> context =
                commandFactory.createDeactivateContext(command);
        BrandMapping brandMapping = validator.findExistingOrThrow(context.id());
        brandMapping.deactivate(context.changedAt());
        commandManager.persist(brandMapping);
    }
}
