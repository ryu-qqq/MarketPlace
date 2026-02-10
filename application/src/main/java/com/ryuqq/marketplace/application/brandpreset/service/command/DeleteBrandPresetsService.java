package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCommandManager;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.DeleteBrandPresetsUseCase;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 벌크 비활성화 Service. */
@Service
public class DeleteBrandPresetsService implements DeleteBrandPresetsUseCase {

    private final BrandPresetReadManager readManager;
    private final BrandPresetCommandFactory commandFactory;
    private final BrandPresetCommandManager commandManager;

    public DeleteBrandPresetsService(
            BrandPresetReadManager readManager,
            BrandPresetCommandFactory commandFactory,
            BrandPresetCommandManager commandManager) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public int execute(DeleteBrandPresetsCommand command) {
        List<BrandPreset> brandPresets = readManager.findAllByIds(command.ids());
        Instant now = commandFactory.now();

        for (BrandPreset brandPreset : brandPresets) {
            brandPreset.deactivate(now);
        }

        commandManager.persistAll(brandPresets);
        return brandPresets.size();
    }
}
