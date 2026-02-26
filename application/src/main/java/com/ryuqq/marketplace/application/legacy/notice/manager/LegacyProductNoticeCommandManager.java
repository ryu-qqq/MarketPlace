package com.ryuqq.marketplace.application.legacy.notice.manager;

import com.ryuqq.marketplace.application.legacy.notice.port.out.command.LegacyProductNoticeCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 고시정보 Command Manager. */
@Component
public class LegacyProductNoticeCommandManager {

    private final LegacyProductNoticeCommandPort commandPort;

    public LegacyProductNoticeCommandManager(LegacyProductNoticeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(LegacyProductGroupId productGroupId, LegacyProductNotice notice) {
        commandPort.persist(productGroupId, notice);
    }
}
