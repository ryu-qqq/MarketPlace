package com.ryuqq.marketplace.application.sellerapplication.service.command;

import com.ryuqq.marketplace.application.common.component.TransactionEventRegistry;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.factory.SellerApplicationCommandFactory;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationCommandManager;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApplySellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.validator.SellerApplicationValidator;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import org.springframework.stereotype.Service;

/**
 * ApplySellerApplicationService - 셀러 입점 신청 Service.
 *
 * <p>새로운 입점 신청을 생성합니다.
 *
 * @author ryu-qqq
 */
@Service
public class ApplySellerApplicationService implements ApplySellerApplicationUseCase {

    private final SellerApplicationCommandFactory commandFactory;
    private final SellerApplicationCommandManager commandManager;
    private final SellerApplicationValidator validator;
    private final TransactionEventRegistry eventRegistry;

    public ApplySellerApplicationService(
            SellerApplicationCommandFactory commandFactory,
            SellerApplicationCommandManager commandManager,
            SellerApplicationValidator validator,
            TransactionEventRegistry eventRegistry) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public Long execute(ApplySellerApplicationCommand command) {
        validator.validateNoPendingApplication(command.businessInfo().registrationNumber());

        SellerApplication application = commandFactory.create(command);
        Long applicationId = commandManager.persist(application);

        application.pollEvents().forEach(eventRegistry::registerForPublish);

        return applicationId;
    }
}
