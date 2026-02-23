package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.BatchRegisterInboundBrandMappingUseCase;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 일괄 등록 Service. */
@Service
public class BatchRegisterInboundBrandMappingService
        implements BatchRegisterInboundBrandMappingUseCase {

    private final InboundBrandMappingValidator validator;
    private final InboundBrandMappingCommandFactory commandFactory;
    private final InboundBrandMappingCommandManager commandManager;

    public BatchRegisterInboundBrandMappingService(
            InboundBrandMappingValidator validator,
            InboundBrandMappingCommandFactory commandFactory,
            InboundBrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public List<Long> execute(BatchRegisterInboundBrandMappingCommand command) {
        List<String> codes =
                command.entries().stream()
                        .map(
                                BatchRegisterInboundBrandMappingCommand.MappingEntry
                                        ::externalBrandCode)
                        .toList();
        validator.validateNotDuplicateBulk(command.inboundSourceId(), codes);

        List<InboundBrandMapping> mappings = commandFactory.createAll(command);
        return commandManager.persistAll(mappings);
    }
}
