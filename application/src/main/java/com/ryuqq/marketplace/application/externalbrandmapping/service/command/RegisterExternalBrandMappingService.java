package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.RegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 등록 Service. */
@Service
public class RegisterExternalBrandMappingService implements RegisterExternalBrandMappingUseCase {

    private final ExternalBrandMappingCommandFactory commandFactory;
    private final ExternalBrandMappingCommandManager commandManager;

    public RegisterExternalBrandMappingService(
            ExternalBrandMappingCommandFactory commandFactory,
            ExternalBrandMappingCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterExternalBrandMappingCommand command) {
        ExternalBrandMapping mapping = commandFactory.create(command);
        return commandManager.persist(mapping);
    }
}
