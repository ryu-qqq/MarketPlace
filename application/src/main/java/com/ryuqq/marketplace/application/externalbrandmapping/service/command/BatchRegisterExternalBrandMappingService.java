package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.BatchRegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 일괄 등록 Service. */
@Service
public class BatchRegisterExternalBrandMappingService
        implements BatchRegisterExternalBrandMappingUseCase {

    private final ExternalBrandMappingCommandFactory commandFactory;
    private final ExternalBrandMappingCommandManager commandManager;

    public BatchRegisterExternalBrandMappingService(
            ExternalBrandMappingCommandFactory commandFactory,
            ExternalBrandMappingCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public List<Long> execute(BatchRegisterExternalBrandMappingCommand command) {
        List<ExternalBrandMapping> mappings = commandFactory.createAll(command);
        return commandManager.persistAll(mappings);
    }
}
