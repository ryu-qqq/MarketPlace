package com.ryuqq.marketplace.application.externalcategorymapping.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Command Factory. */
@Component
public class ExternalCategoryMappingCommandFactory {

    private final TimeProvider timeProvider;

    public ExternalCategoryMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ExternalCategoryMapping create(RegisterExternalCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return ExternalCategoryMapping.forNew(
                command.externalSourceId(),
                command.externalCategoryCode(),
                command.externalCategoryName(),
                command.internalCategoryId(),
                now);
    }

    public List<ExternalCategoryMapping> createAll(
            BatchRegisterExternalCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                ExternalCategoryMapping.forNew(
                                        command.externalSourceId(),
                                        entry.externalCategoryCode(),
                                        entry.externalCategoryName(),
                                        entry.internalCategoryId(),
                                        now))
                .toList();
    }
}
