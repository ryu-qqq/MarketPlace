package com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.condition.ProductNoticeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.QProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.QProductNoticeJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductNotice QueryDSL Repository.
 *
 * <p>PER-ADP-001: QueryAdapter 전용 QueryDSL 레포지토리입니다.
 */
@Repository
public class ProductNoticeQueryDslRepository {

    private static final QProductNoticeJpaEntity productNotice =
            QProductNoticeJpaEntity.productNoticeJpaEntity;

    private static final QProductNoticeEntryJpaEntity productNoticeEntry =
            QProductNoticeEntryJpaEntity.productNoticeEntryJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ProductNoticeConditionBuilder conditionBuilder;

    public ProductNoticeQueryDslRepository(
            JPAQueryFactory queryFactory, ProductNoticeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * productGroupId로 상품 고시정보 조회.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 고시정보 엔티티 (Optional)
     */
    public Optional<ProductNoticeJpaEntity> findByProductGroupId(Long productGroupId) {
        ProductNoticeJpaEntity entity =
                queryFactory
                        .selectFrom(productNotice)
                        .where(conditionBuilder.productGroupIdEq(productGroupId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * productNoticeId로 고시정보 항목 목록 조회.
     *
     * @param productNoticeId 고시정보 ID
     * @return 고시정보 항목 엔티티 목록
     */
    public List<ProductNoticeEntryJpaEntity> findEntriesByProductNoticeId(Long productNoticeId) {
        return queryFactory
                .selectFrom(productNoticeEntry)
                .where(productNoticeEntry.productNoticeId.eq(productNoticeId))
                .fetch();
    }
}
