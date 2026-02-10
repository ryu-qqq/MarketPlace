package com.ryuqq.marketplace.application.saleschannel.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelCommandFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelCommandManager;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.UpdateSalesChannelUseCase;
import com.ryuqq.marketplace.application.saleschannel.validator.SalesChannelValidator;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannelUpdateData;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Service;

/**
 * 판매채널 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 */
@Service
public class UpdateSalesChannelService implements UpdateSalesChannelUseCase {

    private final SalesChannelCommandFactory commandFactory;
    private final SalesChannelCommandManager commandManager;
    private final SalesChannelValidator validator;

    public UpdateSalesChannelService(
            SalesChannelCommandFactory commandFactory,
            SalesChannelCommandManager commandManager,
            SalesChannelValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(UpdateSalesChannelCommand command) {
        UpdateContext<SalesChannelId, SalesChannelUpdateData> context =
                commandFactory.createUpdateContext(command);
        SalesChannelId salesChannelId = context.id();

        SalesChannel salesChannel = validator.findExistingOrThrow(salesChannelId);

        salesChannel.update(context.updateData(), context.changedAt());

        commandManager.persist(salesChannel);
    }
}
