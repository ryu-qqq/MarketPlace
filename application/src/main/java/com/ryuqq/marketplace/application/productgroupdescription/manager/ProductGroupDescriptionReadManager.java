package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.ProductGroupDescriptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroupDescription Read Manager. */
@Component
public class ProductGroupDescriptionReadManager {

    private final ProductGroupDescriptionQueryPort queryPort;

    public ProductGroupDescriptionReadManager(ProductGroupDescriptionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId) {
        return queryPort.findByProductGroupId(productGroupId);
    }
}
