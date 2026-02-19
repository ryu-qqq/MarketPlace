package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.BatchRegisterExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 일괄 등록 Service. */
@Service
public class BatchRegisterExternalCategoryMappingService
        implements BatchRegisterExternalCategoryMappingUseCase {

    private final ExternalCategoryMappingCommandFactory commandFactory;
    private final ExternalCategoryMappingCommandManager commandManager;

    public BatchRegisterExternalCategoryMappingService(
            ExternalCategoryMappingCommandFactory commandFactory,
            ExternalCategoryMappingCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public List<Long> execute(BatchRegisterExternalCategoryMappingCommand command) {
        List<ExternalCategoryMapping> mappings = commandFactory.createAll(command);
        return commandManager.persistAll(mappings);
    }
}
