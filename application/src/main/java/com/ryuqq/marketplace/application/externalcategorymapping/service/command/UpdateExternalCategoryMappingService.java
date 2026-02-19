package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.UpdateExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.application.externalcategorymapping.validator.ExternalCategoryMappingValidator;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingUpdateData;
import org.springframework.stereotype.Service;

/**
 * 외부 카테고리 매핑 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 */
@Service
public class UpdateExternalCategoryMappingService implements UpdateExternalCategoryMappingUseCase {

    private final ExternalCategoryMappingValidator validator;
    private final ExternalCategoryMappingCommandFactory commandFactory;
    private final ExternalCategoryMappingCommandManager commandManager;

    public UpdateExternalCategoryMappingService(
            ExternalCategoryMappingValidator validator,
            ExternalCategoryMappingCommandFactory commandFactory,
            ExternalCategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateExternalCategoryMappingCommand command) {
        UpdateContext<Long, ExternalCategoryMappingUpdateData> context =
                commandFactory.createUpdateContext(command);

        ExternalCategoryMapping mapping = validator.findExistingOrThrow(context.id());
        mapping.update(context.updateData(), context.changedAt());

        commandManager.persist(mapping);
    }
}
