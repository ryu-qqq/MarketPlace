package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.command.SellerOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerOptionGroup Command Manager. */
@Component
public class SellerOptionGroupCommandManager {

    private final SellerOptionGroupCommandPort commandPort;

    public SellerOptionGroupCommandManager(SellerOptionGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional(readOnly = true)
    public List<Long> findGroupIdsByProductGroupId(Long productGroupId) {
        return commandPort.findGroupIdsByProductGroupId(productGroupId);
    }

    @Transactional
    public void deleteByProductGroupId(Long productGroupId) {
        commandPort.deleteByProductGroupId(productGroupId);
    }

    @Transactional
    public Long persist(Long productGroupId, SellerOptionGroup group) {
        return commandPort.persist(productGroupId, group);
    }
}
