package com.ryuqq.marketplace.application.legacy.productnotice.manager;

import com.ryuqq.marketplace.application.legacy.productnotice.port.out.command.LegacyProductNoticeCommandPort;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품 고시정보 저장 매니저. */
@Component
public class LegacyProductNoticeCommandManager {

    private final LegacyProductNoticeCommandPort commandPort;

    public LegacyProductNoticeCommandManager(LegacyProductNoticeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void update(UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        commandPort.update(command, noticeCategory);
    }
}
