package com.ryuqq.marketplace.application.externalsource.service.command;

import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceCommandFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceCommandManager;
import com.ryuqq.marketplace.application.externalsource.port.in.command.RegisterExternalSourceUseCase;
import com.ryuqq.marketplace.application.externalsource.validator.ExternalSourceValidator;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import org.springframework.stereotype.Service;

/** 외부 소스 등록 Service. */
@Service
public class RegisterExternalSourceService implements RegisterExternalSourceUseCase {

    private final ExternalSourceValidator validator;
    private final ExternalSourceCommandFactory commandFactory;
    private final ExternalSourceCommandManager commandManager;

    public RegisterExternalSourceService(
            ExternalSourceValidator validator,
            ExternalSourceCommandFactory commandFactory,
            ExternalSourceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterExternalSourceCommand command) {
        validator.validateCodeNotDuplicate(command.code());
        ExternalSource externalSource = commandFactory.create(command);
        return commandManager.persist(externalSource);
    }
}
