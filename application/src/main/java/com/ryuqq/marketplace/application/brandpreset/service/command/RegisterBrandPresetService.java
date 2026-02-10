package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCommandManager;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.RegisterBrandPresetUseCase;
import com.ryuqq.marketplace.application.brandpreset.validator.BrandPresetValidator;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 등록 Service. */
@Service
public class RegisterBrandPresetService implements RegisterBrandPresetUseCase {

    private final BrandPresetValidator validator;
    private final BrandPresetCommandFactory commandFactory;
    private final BrandPresetCommandManager commandManager;

    public RegisterBrandPresetService(
            BrandPresetValidator validator,
            BrandPresetCommandFactory commandFactory,
            BrandPresetCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(RegisterBrandPresetCommand command) {
        validator.validateSameChannel(command.shopId(), command.salesChannelBrandId());

        BrandPreset brandPreset = commandFactory.create(command);
        return commandManager.persist(brandPreset);
    }
}
