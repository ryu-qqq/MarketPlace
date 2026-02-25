package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDescriptionUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 상세설명 수정 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductUpdateDescriptionService
        implements LegacyProductUpdateDescriptionUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductDescriptionCommandManager descriptionCommandManager;
    private final LegacyProductGroupCommandManager commandManager;

    public LegacyProductUpdateDescriptionService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupReadManager readManager,
            LegacyProductDescriptionCommandManager descriptionCommandManager,
            LegacyProductGroupCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.descriptionCommandManager = descriptionCommandManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(LegacyUpdateDescriptionCommand command) {
        UpdateContext<LegacyProductGroupId, LegacyProductDescription> context =
                commandFactory.createDescriptionUpdateContext(command);

        LegacyProductGroup productGroup = readManager.getById(context.id());
        productGroup.updateDescription(context.updateData(), context.changedAt());

        descriptionCommandManager.persist(context.id(), productGroup.description());
        commandManager.persist(productGroup);
    }
}
