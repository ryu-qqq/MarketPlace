package com.ryuqq.marketplace.application.saleschannelbrand.service.command;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import com.ryuqq.marketplace.application.saleschannelbrand.factory.SalesChannelBrandCommandFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandCommandManager;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.command.RegisterSalesChannelBrandUseCase;
import com.ryuqq.marketplace.application.saleschannelbrand.validator.SalesChannelBrandValidator;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import org.springframework.stereotype.Service;

/** 외부채널 브랜드 등록 Service. */
@Service
public class RegisterSalesChannelBrandService implements RegisterSalesChannelBrandUseCase {

    private final SalesChannelBrandValidator validator;
    private final SalesChannelBrandCommandFactory commandFactory;
    private final SalesChannelBrandCommandManager commandManager;

    public RegisterSalesChannelBrandService(
            SalesChannelBrandValidator validator,
            SalesChannelBrandCommandFactory commandFactory,
            SalesChannelBrandCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterSalesChannelBrandCommand command) {
        validator.validateExternalCodeNotDuplicate(
                command.salesChannelId(), command.externalBrandCode());

        SalesChannelBrand brand = commandFactory.create(command);
        return commandManager.persist(brand);
    }
}
