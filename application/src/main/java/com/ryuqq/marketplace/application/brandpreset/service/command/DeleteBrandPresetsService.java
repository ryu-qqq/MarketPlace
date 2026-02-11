package com.ryuqq.marketplace.application.brandpreset.service.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.DeleteBrandPresetsUseCase;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.util.List;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 벌크 비활성화 Service. */
@Service
public class DeleteBrandPresetsService implements DeleteBrandPresetsUseCase {

    private final BrandPresetReadManager readManager;
    private final BrandPresetCommandFactory commandFactory;
    private final BrandPresetMappingFacade facade;

    public DeleteBrandPresetsService(
            BrandPresetReadManager readManager,
            BrandPresetCommandFactory commandFactory,
            BrandPresetMappingFacade facade) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.facade = facade;
    }

    @Override
    public int execute(DeleteBrandPresetsCommand command) {
        StatusChangeContext<List<Long>> context =
                commandFactory.createDeactivateContext(command);
        List<BrandPreset> brandPresets = readManager.findAllByIds(context.id());
        return facade.deactivateWithMappings(brandPresets, context.changedAt());
    }
}
