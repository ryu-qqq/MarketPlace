package com.ryuqq.marketplace.application.externalsource.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceCommandManager;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.in.command.UpdateExternalSourceUseCase;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 외부 소스 수정 Service. */
@Service
public class UpdateExternalSourceService implements UpdateExternalSourceUseCase {

    private final ExternalSourceReadManager readManager;
    private final ExternalSourceCommandManager commandManager;
    private final TimeProvider timeProvider;

    public UpdateExternalSourceService(
            ExternalSourceReadManager readManager,
            ExternalSourceCommandManager commandManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(UpdateExternalSourceCommand command) {
        ExternalSource externalSource = readManager.getById(command.externalSourceId());
        Instant now = timeProvider.now();

        externalSource.update(command.name(), command.description(), now);

        if (command.status() != null && !command.status().isBlank()) {
            ExternalSourceStatus status = ExternalSourceStatus.fromString(command.status());
            if (status == ExternalSourceStatus.ACTIVE) {
                externalSource.activate(now);
            } else {
                externalSource.deactivate(now);
            }
        }

        commandManager.persist(externalSource);
    }
}
