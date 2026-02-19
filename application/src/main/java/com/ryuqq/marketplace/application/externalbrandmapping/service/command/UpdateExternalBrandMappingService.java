package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.UpdateExternalBrandMappingUseCase;
import com.ryuqq.marketplace.application.externalbrandmapping.validator.ExternalBrandMappingValidator;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingUpdateData;
import org.springframework.stereotype.Service;

/**
 * 외부 브랜드 매핑 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 */
@Service
public class UpdateExternalBrandMappingService implements UpdateExternalBrandMappingUseCase {

    private final ExternalBrandMappingValidator validator;
    private final ExternalBrandMappingCommandFactory commandFactory;
    private final ExternalBrandMappingCommandManager commandManager;

    public UpdateExternalBrandMappingService(
            ExternalBrandMappingValidator validator,
            ExternalBrandMappingCommandFactory commandFactory,
            ExternalBrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateExternalBrandMappingCommand command) {
        UpdateContext<Long, ExternalBrandMappingUpdateData> context =
                commandFactory.createUpdateContext(command);

        ExternalBrandMapping mapping = validator.findExistingOrThrow(context.id());
        mapping.update(context.updateData(), context.changedAt());

        commandManager.persist(mapping);
    }
}
