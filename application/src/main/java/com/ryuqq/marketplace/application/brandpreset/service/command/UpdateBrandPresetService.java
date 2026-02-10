package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCommandManager;
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
    private final BrandPresetCommandManager commandManager;

    public UpdateBrandPresetService(
            BrandPresetValidator validator,
            BrandPresetCommandFactory commandFactory,
            BrandPresetCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateBrandPresetCommand command) {
        BrandPreset brandPreset =
                validator.findExistingOrThrow(BrandPresetId.of(command.brandPresetId()));
        brandPreset.update(
                command.presetName(), command.salesChannelBrandId(), commandFactory.now());
        commandManager.persist(brandPreset);
    }
}
