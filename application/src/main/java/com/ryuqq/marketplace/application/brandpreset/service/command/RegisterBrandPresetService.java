package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.RegisterBrandPresetUseCase;
import com.ryuqq.marketplace.application.brandpreset.validator.BrandPresetValidator;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 등록 Service. */
@Service
public class RegisterBrandPresetService implements RegisterBrandPresetUseCase {

    private final BrandPresetValidator validator;
    private final BrandPresetCommandFactory commandFactory;
    private final BrandPresetMappingFacade facade;

    public RegisterBrandPresetService(
            BrandPresetValidator validator,
            BrandPresetCommandFactory commandFactory,
            BrandPresetMappingFacade facade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public Long execute(RegisterBrandPresetCommand command) {
        validator.validateSameChannel(command.shopId(), command.salesChannelBrandId());
        validator.validateInternalBrandsExist(command.internalBrandIds());

        RegisterBrandPresetBundle bundle = commandFactory.createRegisterBundle(command);
        return facade.registerWithMappings(bundle);
    }
}
