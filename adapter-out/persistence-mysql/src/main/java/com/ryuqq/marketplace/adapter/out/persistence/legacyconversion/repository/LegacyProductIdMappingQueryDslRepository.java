package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyProductIdMappingJpaEntity.legacyProductIdMappingJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LegacyProductIdMappingQueryDslRepository - QueryDSL 기반 조회 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class LegacyProductIdMappingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductIdMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * legacyProductId로 매핑 조회.
     *
     * @param legacyProductId 레거시 Product(SKU) ID
     * @return 매핑 Optional
     */
    public Optional<LegacyProductIdMappingJpaEntity> findByLegacyProductId(long legacyProductId) {
        LegacyProductIdMappingJpaEntity entity =
                queryFactory
                        .selectFrom(legacyProductIdMappingJpaEntity)
                        .where(legacyProductIdMappingJpaEntity.legacyProductId.eq(legacyProductId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * internalProductId로 매핑 조회.
     *
     * @param internalProductId 내부 Product ID
     * @return 매핑 Optional
     */
    public Optional<LegacyProductIdMappingJpaEntity> findByInternalProductId(
            long internalProductId) {
        LegacyProductIdMappingJpaEntity entity =
                queryFactory
                        .selectFrom(legacyProductIdMappingJpaEntity)
                        .where(
                                legacyProductIdMappingJpaEntity.internalProductId.eq(
                                        internalProductId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * legacyProductGroupId로 해당 그룹의 모든 SKU 매핑 조회.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return SKU 매핑 목록
     */
    public List<LegacyProductIdMappingJpaEntity> findByLegacyProductGroupId(
            long legacyProductGroupId) {
        return queryFactory
                .selectFrom(legacyProductIdMappingJpaEntity)
                .where(
                        legacyProductIdMappingJpaEntity.legacyProductGroupId.eq(
                                legacyProductGroupId))
                .fetch();
    }

    /**
     * 여러 legacyProductGroupId로 매핑 일괄 조회.
     *
     * @param legacyProductGroupIds 레거시 상품그룹 ID 목록
     * @return 매핑 목록
     */
    public List<LegacyProductIdMappingJpaEntity> findByLegacyProductGroupIds(
            Collection<Long> legacyProductGroupIds) {
        if (legacyProductGroupIds == null || legacyProductGroupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(legacyProductIdMappingJpaEntity)
                .where(
                        legacyProductIdMappingJpaEntity.legacyProductGroupId.in(
                                legacyProductGroupIds))
                .fetch();
    }
}
