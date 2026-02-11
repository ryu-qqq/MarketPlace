package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.UpdateBrandPresetUseCase;
import com.ryuqq.marketplace.application.brandpreset.validator.BrandPresetValidator;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 수정 Service. */
@Service
public class UpdateBrandPresetService implements UpdateBrandPresetUseCase {

    private final BrandPresetValidator validator;
    private final BrandPresetCommandFactory commandFactory;
    private final BrandPresetMappingFacade facade;

    public UpdateBrandPresetService(
            BrandPresetValidator validator,
            BrandPresetCommandFactory commandFactory,
            BrandPresetMappingFacade facade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public void execute(UpdateBrandPresetCommand command) {
        BrandPreset existing =
                validator.findExistingOrThrow(BrandPresetId.of(command.brandPresetId()));
        validator.validateSameChannel(existing.shopId(), command.salesChannelBrandId());
        validator.validateInternalBrandsExist(command.internalBrandIds());

        UpdateBrandPresetBundle bundle = commandFactory.createUpdateBundle(existing, command);
        facade.updateWithMappings(bundle);
    }
}
