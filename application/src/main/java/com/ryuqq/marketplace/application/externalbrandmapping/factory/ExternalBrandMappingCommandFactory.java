package com.ryuqq.marketplace.application.externalbrandmapping.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Command Factory. */
@Component
public class ExternalBrandMappingCommandFactory {

    private final TimeProvider timeProvider;

    public ExternalBrandMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ExternalBrandMapping create(RegisterExternalBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return ExternalBrandMapping.forNew(
                command.externalSourceId(),
                command.externalBrandCode(),
                command.externalBrandName(),
                command.internalBrandId(),
                now);
    }

    public List<ExternalBrandMapping> createAll(BatchRegisterExternalBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                ExternalBrandMapping.forNew(
                                        command.externalSourceId(),
                                        entry.externalBrandCode(),
                                        entry.externalBrandName(),
                                        entry.internalBrandId(),
                                        now))
                .toList();
    }
}
