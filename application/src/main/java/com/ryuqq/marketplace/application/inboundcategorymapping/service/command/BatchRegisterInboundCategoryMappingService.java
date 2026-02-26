package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.BatchRegisterInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 일괄 등록 Service. */
@Service
public class BatchRegisterInboundCategoryMappingService
        implements BatchRegisterInboundCategoryMappingUseCase {

    private final InboundCategoryMappingValidator validator;
    private final InboundCategoryMappingCommandFactory commandFactory;
    private final InboundCategoryMappingCommandManager commandManager;

    public BatchRegisterInboundCategoryMappingService(
            InboundCategoryMappingValidator validator,
            InboundCategoryMappingCommandFactory commandFactory,
            InboundCategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public List<Long> execute(BatchRegisterInboundCategoryMappingCommand command) {
        List<String> codes =
                command.entries().stream()
                        .map(
                                BatchRegisterInboundCategoryMappingCommand.MappingEntry
                                        ::externalCategoryCode)
                        .toList();
        validator.validateNotDuplicateBulk(command.inboundSourceId(), codes);

        List<InboundCategoryMapping> mappings = commandFactory.createAll(command);
        return commandManager.persistAll(mappings);
    }
}
