package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.RegisterCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 등록 Service. */
@Service
public class RegisterCategoryPresetService implements RegisterCategoryPresetUseCase {

    private final CategoryPresetValidator validator;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetMappingFacade facade;

    public RegisterCategoryPresetService(
            CategoryPresetValidator validator,
            CategoryPresetCommandFactory commandFactory,
            CategoryPresetMappingFacade facade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public Long execute(RegisterCategoryPresetCommand command) {
        Long salesChannelCategoryId =
                validator.resolveSalesChannelCategoryId(command.shopId(), command.categoryCode());
        validator.validateInternalCategoriesExist(command.internalCategoryIds());

        RegisterCategoryPresetBundle bundle =
                commandFactory.createRegisterBundle(command, salesChannelCategoryId);
        return facade.registerWithMappings(bundle);
    }
}
