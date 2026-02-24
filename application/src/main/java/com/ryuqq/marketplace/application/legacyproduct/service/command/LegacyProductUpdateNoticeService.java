package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyNoticeUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateNoticeUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 고시정보 수정 서비스.
 *
 * <p>LegacyNoticeUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateNoticeService implements LegacyProductUpdateNoticeUseCase {

    private final LegacyNoticeUpdateCoordinator noticeUpdateCoordinator;

    public LegacyProductUpdateNoticeService(LegacyNoticeUpdateCoordinator noticeUpdateCoordinator) {
        this.noticeUpdateCoordinator = noticeUpdateCoordinator;
    }

    @Override
    public void execute(LegacyUpdateNoticeCommand command) {
        noticeUpdateCoordinator.execute(command);
    }
}
