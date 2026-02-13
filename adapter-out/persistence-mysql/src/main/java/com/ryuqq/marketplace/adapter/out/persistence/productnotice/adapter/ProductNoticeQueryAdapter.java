package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeQueryDslRepository;
import com.ryuqq.marketplace.application.productnotice.port.out.query.ProductNoticeQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductNoticeQueryAdapter - 상품 고시정보 조회 어댑터.
 *
 * <p>ProductNoticeQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductNoticeQueryAdapter implements ProductNoticeQueryPort {

    private final ProductNoticeQueryDslRepository queryDslRepository;
    private final ProductNoticeJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 의존.
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ProductNoticeQueryAdapter(
            ProductNoticeQueryDslRepository queryDslRepository,
            ProductNoticeJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * 상품 그룹 ID로 고시정보 조회.
     *
     * <p>부모 엔티티 조회 후, 자식 항목을 추가 조회하여 도메인으로 변환합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 고시정보 도메인 객체 (Optional)
     */
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
}
