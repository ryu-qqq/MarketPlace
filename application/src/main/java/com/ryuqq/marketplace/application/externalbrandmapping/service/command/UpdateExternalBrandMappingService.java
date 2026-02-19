package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.UpdateExternalBrandMappingUseCase;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 수정 Service. */
@Service
public class UpdateExternalBrandMappingService implements UpdateExternalBrandMappingUseCase {

    private final ExternalBrandMappingReadManager readManager;
    private final ExternalBrandMappingCommandManager commandManager;
    private final TimeProvider timeProvider;

    public UpdateExternalBrandMappingService(
            ExternalBrandMappingReadManager readManager,
            ExternalBrandMappingCommandManager commandManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(UpdateExternalBrandMappingCommand command) {
        ExternalBrandMapping mapping = readManager.getById(command.id());
        Instant now = timeProvider.now();

        mapping.update(command.externalBrandName(), command.internalBrandId(), now);

        if (command.status() != null && !command.status().isBlank()) {
            ExternalBrandMappingStatus status =
                    ExternalBrandMappingStatus.fromString(command.status());
            if (status == ExternalBrandMappingStatus.ACTIVE) {
                mapping.activate(now);
            } else {
                mapping.deactivate(now);
            }
        }

        commandManager.persist(mapping);
    }
}
