package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.UpdateCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 수정 Service. */
@Service
public class UpdateCategoryPresetService implements UpdateCategoryPresetUseCase {

    private final CategoryPresetValidator validator;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetMappingFacade facade;

    public UpdateCategoryPresetService(
            CategoryPresetValidator validator,
            CategoryPresetCommandFactory commandFactory,
            CategoryPresetMappingFacade facade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public void execute(UpdateCategoryPresetCommand command) {
        CategoryPreset existing =
                validator.findExistingOrThrow(CategoryPresetId.of(command.categoryPresetId()));

        Long salesChannelCategoryId =
                validator.resolveSalesChannelCategoryId(existing.shopId(), command.categoryCode());
        validator.validateInternalCategoriesExist(command.internalCategoryIds());

        UpdateCategoryPresetBundle bundle =
                commandFactory.createUpdateBundle(existing, command, salesChannelCategoryId);
        facade.updateWithMappings(bundle);
    }
}
