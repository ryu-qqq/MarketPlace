package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCommandManager;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.DeleteCategoryPresetsUseCase;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 벌크 비활성화 Service. */
@Service
public class DeleteCategoryPresetsService implements DeleteCategoryPresetsUseCase {

    private final CategoryPresetReadManager readManager;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetCommandManager commandManager;

    public DeleteCategoryPresetsService(
            CategoryPresetReadManager readManager,
            CategoryPresetCommandFactory commandFactory,
            CategoryPresetCommandManager commandManager) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public int execute(DeleteCategoryPresetsCommand command) {
        List<CategoryPreset> presets = readManager.findAllByIds(command.ids());
        if (presets.isEmpty()) {
            return 0;
        }

        Instant now = commandFactory.now();
        presets.forEach(preset -> preset.deactivate(now));
        commandManager.persistAll(presets);
        return presets.size();
    }
}
