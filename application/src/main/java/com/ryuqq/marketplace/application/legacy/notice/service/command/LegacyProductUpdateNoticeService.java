package com.ryuqq.marketplace.application.legacy.notice.service.command;

import com.ryuqq.marketplace.application.legacy.notice.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.application.legacy.notice.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 고시정보 수정 서비스.
 *
 * <p>Manager에 표준 커맨드를 위임하고 ConversionOutbox를 생성합니다.
 */
@Service
public class LegacyProductUpdateNoticeService implements LegacyProductUpdateNoticeUseCase {

    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateNoticeService(
            LegacyProductNoticeCommandManager noticeCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.noticeCommandManager = noticeCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        noticeCommandManager.update(command, noticeCategory);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
