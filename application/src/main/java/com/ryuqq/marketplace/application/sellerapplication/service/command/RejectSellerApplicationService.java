package com.ryuqq.marketplace.application.sellerapplication.service.command;

import com.ryuqq.marketplace.application.common.component.TransactionEventRegistry;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.factory.SellerApplicationCommandFactory;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationCommandManager;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.RejectSellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.validator.SellerApplicationValidator;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import org.springframework.stereotype.Service;

/**
 * RejectSellerApplicationService - 셀러 입점 신청 거절 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>대기 상태의 입점 신청을 거절합니다.
 *
 * @author ryu-qqq
 */
@Service
public class RejectSellerApplicationService implements RejectSellerApplicationUseCase {

    private final SellerApplicationValidator validator;
    private final SellerApplicationCommandManager commandManager;
    private final SellerApplicationCommandFactory commandFactory;
    private final TransactionEventRegistry eventRegistry;

    public RejectSellerApplicationService(
            SellerApplicationValidator validator,
            SellerApplicationCommandManager commandManager,
            SellerApplicationCommandFactory commandFactory,
            TransactionEventRegistry eventRegistry) {
        this.validator = validator;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void execute(RejectSellerApplicationCommand command) {
        StatusChangeContext<SellerApplicationId> context =
                commandFactory.createRejectContext(command);

        SellerApplication application = validator.findExistingOrThrow(context.id());

        application.reject(command.rejectionReason(), command.processedBy(), context.changedAt());
        commandManager.persist(application);

        application.pollEvents().forEach(eventRegistry::registerForPublish);
    }
}
