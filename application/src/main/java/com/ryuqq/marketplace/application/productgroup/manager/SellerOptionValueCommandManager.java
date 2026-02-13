package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.command.SellerOptionValueCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerOptionValue Command Manager. */
@Component
public class SellerOptionValueCommandManager {

    private final SellerOptionValueCommandPort commandPort;

    public SellerOptionValueCommandManager(SellerOptionValueCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void deleteByGroupIdIn(List<Long> groupIds) {
        commandPort.deleteByGroupIdIn(groupIds);
    }

    @Transactional
    public void persistAll(Long groupId, List<SellerOptionValue> values) {
        commandPort.persistAll(groupId, values);
    }
}
