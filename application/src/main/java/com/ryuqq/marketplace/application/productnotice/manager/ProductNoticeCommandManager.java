package com.ryuqq.marketplace.application.productnotice.manager;

import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeCommandPort;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductNotice Command Manager. */
@Component
public class ProductNoticeCommandManager {

    private final ProductNoticeCommandPort commandPort;

    public ProductNoticeCommandManager(ProductNoticeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductNotice productNotice) {
        return commandPort.persist(productNotice);
    }
}
