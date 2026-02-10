package com.ryuqq.marketplace.application.saleschannelcategory.service.command;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import com.ryuqq.marketplace.application.saleschannelcategory.factory.SalesChannelCategoryCommandFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryCommandManager;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.command.RegisterSalesChannelCategoryUseCase;
import com.ryuqq.marketplace.application.saleschannelcategory.validator.SalesChannelCategoryValidator;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import org.springframework.stereotype.Service;

/** 외부 채널 카테고리 등록 Service. */
@Service
public class RegisterSalesChannelCategoryService implements RegisterSalesChannelCategoryUseCase {

    private final SalesChannelCategoryValidator validator;
    private final SalesChannelCategoryCommandFactory commandFactory;
    private final SalesChannelCategoryCommandManager commandManager;

    public RegisterSalesChannelCategoryService(
            SalesChannelCategoryValidator validator,
            SalesChannelCategoryCommandFactory commandFactory,
            SalesChannelCategoryCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterSalesChannelCategoryCommand command) {
        validator.validateExternalCodeNotDuplicate(
                command.salesChannelId(), command.externalCategoryCode());

        SalesChannelCategory category = commandFactory.create(command);
        return commandManager.persist(category);
    }
}
