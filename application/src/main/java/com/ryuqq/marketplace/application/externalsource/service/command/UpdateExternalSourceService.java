package com.ryuqq.marketplace.application.externalsource.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceCommandFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceCommandManager;
import com.ryuqq.marketplace.application.externalsource.port.in.command.UpdateExternalSourceUseCase;
import com.ryuqq.marketplace.application.externalsource.validator.ExternalSourceValidator;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceUpdateData;
import org.springframework.stereotype.Service;

/**
 * 외부 소스 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 */
@Service
public class UpdateExternalSourceService implements UpdateExternalSourceUseCase {

    private final ExternalSourceValidator validator;
    private final ExternalSourceCommandFactory commandFactory;
    private final ExternalSourceCommandManager commandManager;

    public UpdateExternalSourceService(
            ExternalSourceValidator validator,
            ExternalSourceCommandFactory commandFactory,
            ExternalSourceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateExternalSourceCommand command) {
        UpdateContext<Long, ExternalSourceUpdateData> context =
                commandFactory.createUpdateContext(command);

        ExternalSource externalSource = validator.findExistingOrThrow(context.id());
        externalSource.update(context.updateData(), context.changedAt());

        commandManager.persist(externalSource);
    }
}
