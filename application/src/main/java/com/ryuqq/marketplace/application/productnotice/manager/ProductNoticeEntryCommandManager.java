package com.ryuqq.marketplace.application.productnotice.manager;

import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeEntryCommandPort;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductNoticeEntry Command Manager. */
@Component
public class ProductNoticeEntryCommandManager {

    private final ProductNoticeEntryCommandPort commandPort;

    public ProductNoticeEntryCommandManager(ProductNoticeEntryCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persistAll(List<ProductNoticeEntry> entries) {
        for (ProductNoticeEntry entry : entries) {
            commandPort.persist(entry);
        }
    }
}
