package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.UpdateInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
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
public class UpdateInboundCategoryMappingService implements UpdateInboundCategoryMappingUseCase {

    private final InboundCategoryMappingValidator validator;
    private final InboundCategoryMappingCommandFactory commandFactory;
    private final InboundCategoryMappingCommandManager commandManager;

    public UpdateInboundCategoryMappingService(
            InboundCategoryMappingValidator validator,
            InboundCategoryMappingCommandFactory commandFactory,
            InboundCategoryMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateInboundCategoryMappingCommand command) {
        UpdateContext<Long, InboundCategoryMappingUpdateData> context =
                commandFactory.createUpdateContext(command);

        InboundCategoryMapping mapping = validator.findExistingOrThrow(context.id());
        mapping.update(context.updateData(), context.changedAt());

        commandManager.persist(mapping);
    }
}
