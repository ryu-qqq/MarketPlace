package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.UpdateExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 수정 Service. */
@Service
public class UpdateExternalCategoryMappingService implements UpdateExternalCategoryMappingUseCase {

    private final ExternalCategoryMappingReadManager readManager;
    private final ExternalCategoryMappingCommandManager commandManager;
    private final TimeProvider timeProvider;

    public UpdateExternalCategoryMappingService(
            ExternalCategoryMappingReadManager readManager,
            ExternalCategoryMappingCommandManager commandManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(UpdateExternalCategoryMappingCommand command) {
        ExternalCategoryMapping mapping = readManager.getById(command.id());
        Instant now = timeProvider.now();

        mapping.update(command.externalCategoryName(), command.internalCategoryId(), now);

        if (command.status() != null && !command.status().isBlank()) {
            ExternalCategoryMappingStatus status =
                    ExternalCategoryMappingStatus.fromString(command.status());
            if (status == ExternalCategoryMappingStatus.ACTIVE) {
                mapping.activate(now);
            } else {
                mapping.deactivate(now);
            }
        }

        commandManager.persist(mapping);
    }
}
