package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 품절 처리 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductMarkOutOfStockService implements LegacyProductMarkOutOfStockUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductMarkOutOfStockService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createMarkOutOfStockContext(command);

        LegacyProductGroup productGroup = readManager.getById(context.id());
        productGroup.markSoldOut(context.changedAt());

        commandManager.persist(productGroup);
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
