package com.ryuqq.marketplace.application.legacy.notice.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacy.notice.internal.LegacyNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.notice.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
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
    private final LegacyNoticeCommandCoordinator noticeCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateNoticeService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyNoticeCommandCoordinator noticeCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateNoticeCommand command) {
        UpdateContext<LegacyProductGroupId, LegacyProductNotice> context =
                commandFactory.createNoticeUpdateContext(command);

        noticeCommandCoordinator.update(context.id(), context.updateData(), context.changedAt());

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
