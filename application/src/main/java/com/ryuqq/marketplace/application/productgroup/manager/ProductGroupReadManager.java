package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroup Read Manager. */
@Component
public class ProductGroupReadManager {

    private final ProductGroupQueryPort queryPort;

    public ProductGroupReadManager(ProductGroupQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ProductGroup getById(ProductGroupId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new ProductGroupNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<ProductGroup> findByCriteria(ProductGroupSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
