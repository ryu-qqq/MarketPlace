package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeQueryDslRepository;
import com.ryuqq.marketplace.application.productnotice.port.out.query.ProductNoticeQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductNoticeQueryAdapter - 상품 고시정보 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>Aggregate Root를 통해 자식(ProductNoticeEntry)까지 완전히 로딩합니다.
 */
@Component
public class ProductNoticeQueryAdapter implements ProductNoticeQueryPort {

    private final ProductNoticeQueryDslRepository queryDslRepository;
    private final ProductNoticeJpaEntityMapper mapper;

    public ProductNoticeQueryAdapter(
            ProductNoticeQueryDslRepository queryDslRepository,
            ProductNoticeJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductNotice> findByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository
                .findByProductGroupId(productGroupId.value())
                .map(
                        entity -> {
                            List<ProductNoticeEntryJpaEntity> entries =
                                    queryDslRepository.findEntriesByProductNoticeId(entity.getId());
                            return mapper.toDomain(entity, entries);
                        });
    }

    @Override
    public List<ProductNotice> findByProductGroupIdIn(List<ProductGroupId> productGroupIds) {
        List<Long> rawIds = productGroupIds.stream().map(ProductGroupId::value).toList();
        List<ProductNoticeJpaEntity> entities = queryDslRepository.findByProductGroupIdIn(rawIds);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> noticeIds = entities.stream().map(ProductNoticeJpaEntity::getId).toList();
        List<ProductNoticeEntryJpaEntity> allEntries =
                queryDslRepository.findEntriesByProductNoticeIds(noticeIds);
        Map<Long, List<ProductNoticeEntryJpaEntity>> entriesByNoticeId =
                allEntries.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ProductNoticeEntryJpaEntity::getProductNoticeId));

        return entities.stream()
                .map(
                        entity -> {
                            List<ProductNoticeEntryJpaEntity> entries =
                                    entriesByNoticeId.getOrDefault(entity.getId(), List.of());
                            return mapper.toDomain(entity, entries);
                        })
                .toList();
    }
}
