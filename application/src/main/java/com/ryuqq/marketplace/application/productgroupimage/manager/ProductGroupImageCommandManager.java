package com.ryuqq.marketplace.application.productgroupimage.manager;

import com.ryuqq.marketplace.application.productgroupimage.port.out.command.ProductGroupImageCommandPort;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.ArrayList;
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
    public Long persist(ProductGroupImage image) {
        return commandPort.persist(image);
    }

    @Transactional
    public List<Long> persistAll(List<ProductGroupImage> images) {
        List<Long> ids = new ArrayList<>();
        for (ProductGroupImage image : images) {
            ids.add(commandPort.persist(image));
        }
        return ids;
    }
}
