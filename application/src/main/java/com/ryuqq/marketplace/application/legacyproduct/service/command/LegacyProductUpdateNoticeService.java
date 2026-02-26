package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 고시정보 수정 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductUpdateNoticeService implements LegacyProductUpdateNoticeUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateNoticeService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupReadManager readManager,
            LegacyProductNoticeCommandManager noticeCommandManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.noticeCommandManager = noticeCommandManager;
        this.commandManager = commandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateNoticeCommand command) {
        UpdateContext<LegacyProductGroupId, LegacyProductNotice> context =
                commandFactory.createNoticeUpdateContext(command);

        LegacyProductGroup productGroup = readManager.getById(context.id());
        productGroup.updateNotice(context.updateData(), context.changedAt());

        noticeCommandManager.persist(context.id(), productGroup.notice());
        commandManager.persist(productGroup);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
