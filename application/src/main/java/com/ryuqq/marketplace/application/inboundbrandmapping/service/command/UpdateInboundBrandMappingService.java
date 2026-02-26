package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.UpdateInboundBrandMappingUseCase;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
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
public class UpdateInboundBrandMappingService implements UpdateInboundBrandMappingUseCase {

    private final InboundBrandMappingValidator validator;
    private final InboundBrandMappingCommandFactory commandFactory;
    private final InboundBrandMappingCommandManager commandManager;

    public UpdateInboundBrandMappingService(
            InboundBrandMappingValidator validator,
            InboundBrandMappingCommandFactory commandFactory,
            InboundBrandMappingCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateInboundBrandMappingCommand command) {
        UpdateContext<Long, InboundBrandMappingUpdateData> context =
                commandFactory.createUpdateContext(command);

        InboundBrandMapping mapping = validator.findExistingOrThrow(context.id());
        mapping.update(context.updateData(), context.changedAt());

        commandManager.persist(mapping);
    }
}
