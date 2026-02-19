package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.ProductGroupDescriptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.util.List;
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
    public ProductGroupDescription getById(Long id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "ProductGroupDescription not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId) {
        return queryPort.findByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public ProductGroupDescription getByProductGroupId(ProductGroupId productGroupId) {
        return queryPort
                .findByProductGroupId(productGroupId)
                .orElseThrow(() -> new ProductGroupDescriptionNotFoundException(productGroupId));
    }

    @Transactional(readOnly = true)
    public List<ProductGroupDescription> findPublishReady(int limit) {
        return queryPort.findByPublishStatus(DescriptionPublishStatus.PUBLISH_READY, limit);
    }
}
