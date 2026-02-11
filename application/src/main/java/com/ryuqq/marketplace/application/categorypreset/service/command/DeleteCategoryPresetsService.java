package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.DeleteCategoryPresetsUseCase;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.util.List;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 벌크 비활성화 Service. */
@Service
public class DeleteCategoryPresetsService implements DeleteCategoryPresetsUseCase {

    private final CategoryPresetReadManager readManager;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetMappingFacade facade;

    public DeleteCategoryPresetsService(
            CategoryPresetReadManager readManager,
            CategoryPresetCommandFactory commandFactory,
            CategoryPresetMappingFacade facade) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public int execute(DeleteCategoryPresetsCommand command) {
        StatusChangeContext<List<Long>> context =
                commandFactory.createDeactivateContext(command);
        List<CategoryPreset> presets = readManager.findAllByIds(context.id());
        return facade.deactivateWithMappings(presets, context.changedAt());
    }
}
