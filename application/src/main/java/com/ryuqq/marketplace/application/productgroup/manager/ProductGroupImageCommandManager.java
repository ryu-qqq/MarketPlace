package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.command.ProductGroupImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroupImage Command Manager. */
@Component
public class ProductGroupImageCommandManager {

    private final ProductGroupImageCommandPort commandPort;

    public ProductGroupImageCommandManager(ProductGroupImageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void deleteByProductGroupId(Long productGroupId) {
        commandPort.deleteByProductGroupId(productGroupId);
    }

    @Transactional
    public List<Long> persistAll(Long productGroupId, List<ProductGroupImage> images) {
        return commandPort.persistAll(productGroupId, images);
    }
}
