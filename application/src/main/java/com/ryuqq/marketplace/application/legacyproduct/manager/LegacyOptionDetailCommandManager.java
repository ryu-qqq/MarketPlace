package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyOptionDetailCommandPort;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 옵션상세 Command Manager. */
@Component
public class LegacyOptionDetailCommandManager {

    private final LegacyOptionDetailCommandPort commandPort;

    public LegacyOptionDetailCommandManager(LegacyOptionDetailCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(LegacyOptionDetail optionDetail) {
        return commandPort.persist(optionDetail);
    }
}
