package com.ryuqq.marketplace.application.brandmapping.service.command;

import com.ryuqq.marketplace.application.brandmapping.dto.command.RegisterBrandMappingCommand;
import com.ryuqq.marketplace.application.brandmapping.factory.BrandMappingCommandFactory;
import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingCommandManager;
import com.ryuqq.marketplace.application.brandmapping.port.in.command.RegisterBrandMappingUseCase;
import com.ryuqq.marketplace.application.brandmapping.validator.BrandMappingValidator;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import org.springframework.stereotype.Service;

/** 브랜드 매핑 등록 Service. */
@Service
public class RegisterBrandMappingService implements RegisterBrandMappingUseCase {

    private final BrandMappingValidator validator;
    private final BrandMappingCommandFactory commandFactory;
    private final BrandMappingCommandManager commandManager;

    public RegisterBrandMappingService(
            BrandMappingValidator validator,
            BrandMappingCommandFactory commandFactory,
            BrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterBrandMappingCommand command) {
        validator.validateNotDuplicate(command.salesChannelBrandId());

        BrandMapping brandMapping = commandFactory.create(command);
        return commandManager.persist(brandMapping);
    }
}
