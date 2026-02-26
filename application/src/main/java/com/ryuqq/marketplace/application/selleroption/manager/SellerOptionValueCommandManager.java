package com.ryuqq.marketplace.application.selleroption.manager;

import com.ryuqq.marketplace.application.selleroption.port.out.command.SellerOptionValueCommandPort;
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
    public Long persist(SellerOptionValue value) {
        return commandPort.persist(value);
    }

    @Transactional
    public List<Long> persistAll(List<SellerOptionValue> values) {
        return commandPort.persistAll(values);
    }

    @Transactional
    public List<Long> persistAllForGroup(Long sellerOptionGroupId, List<SellerOptionValue> values) {
        return commandPort.persistAllForGroup(sellerOptionGroupId, values);
    }
}
