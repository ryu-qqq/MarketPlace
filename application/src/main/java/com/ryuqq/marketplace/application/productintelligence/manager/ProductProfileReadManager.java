package com.ryuqq.marketplace.application.productintelligence.manager;

import com.ryuqq.marketplace.application.productintelligence.port.out.query.ProductProfileQueryPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 상품 프로파일 Read Manager. */
@Component
public class ProductProfileReadManager {

    private final ProductProfileQueryPort queryPort;

    public ProductProfileReadManager(ProductProfileQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<ProductProfile> findById(Long profileId) {
        return queryPort.findById(profileId);
    }

    @Transactional(readOnly = true)
    public Optional<ProductProfile> findLatestByProductGroupId(Long productGroupId) {
        return queryPort.findLatestByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public Optional<ProductProfile> findLatestActiveByProductGroupId(Long productGroupId) {
        return queryPort.findLatestActiveByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public Optional<ProductProfile> findLatestCompletedByProductGroupId(Long productGroupId) {
        return queryPort.findLatestCompletedByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public List<ProductProfile> findAllByProductGroupId(Long productGroupId) {
        return queryPort.findAllByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public List<ProductProfile> findStuckAnalyzingProfiles(
            java.time.Instant stuckThreshold, int limit) {
        return queryPort.findStuckAnalyzingProfiles(stuckThreshold, limit);
    }
}
