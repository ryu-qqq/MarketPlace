package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductOptionCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품옵션매핑 Command Manager. */
@Component
public class LegacyProductOptionCommandManager {

    private final LegacyProductOptionCommandPort commandPort;

    public LegacyProductOptionCommandManager(LegacyProductOptionCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(LegacyProductOption productOption) {
        commandPort.persist(productOption);
    }
}
