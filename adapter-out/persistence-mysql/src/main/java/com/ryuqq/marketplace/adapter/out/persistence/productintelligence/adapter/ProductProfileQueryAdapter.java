package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.ProductProfileJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.ProductProfileQueryDslRepository;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.ProductProfileQueryPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductProfileQueryAdapter - 상품 프로파일 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductProfileQueryAdapter implements ProductProfileQueryPort {

    private final ProductProfileQueryDslRepository queryDslRepository;
    private final ProductProfileJpaEntityMapper mapper;

    public ProductProfileQueryAdapter(
            ProductProfileQueryDslRepository queryDslRepository,
            ProductProfileJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductProfile> findById(Long profileId) {
        return queryDslRepository.findById(profileId).map(mapper::toDomain);
    }

    @Override
    public Optional<ProductProfile> findLatestByProductGroupId(Long productGroupId) {
        return queryDslRepository.findLatestByProductGroupId(productGroupId).map(mapper::toDomain);
    }

    @Override
    public Optional<ProductProfile> findLatestActiveByProductGroupId(Long productGroupId) {
        return queryDslRepository
                .findLatestActiveByProductGroupId(productGroupId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ProductProfile> findLatestCompletedByProductGroupId(Long productGroupId) {
        return queryDslRepository
                .findLatestCompletedByProductGroupId(productGroupId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductProfile> findAllByProductGroupId(Long productGroupId) {
        return queryDslRepository.findAllByProductGroupId(productGroupId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProductProfile> findStuckAnalyzingProfiles(
            java.time.Instant stuckThreshold, int limit) {
        return queryDslRepository.findStuckAnalyzingProfiles(stuckThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
