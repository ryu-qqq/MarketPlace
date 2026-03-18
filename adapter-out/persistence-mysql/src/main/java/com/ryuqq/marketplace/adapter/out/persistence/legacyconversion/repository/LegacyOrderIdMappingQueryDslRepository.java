package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyOrderIdMappingJpaEntity.legacyOrderIdMappingJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LegacyOrderIdMappingQueryDslRepository - QueryDSL 기반 조회 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class LegacyOrderIdMappingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyOrderIdMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * legacyOrderId로 매핑 조회.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 매핑 Optional
     */
    public Optional<LegacyOrderIdMappingJpaEntity> findByLegacyOrderId(long legacyOrderId) {
        LegacyOrderIdMappingJpaEntity entity =
                queryFactory
                        .selectFrom(legacyOrderIdMappingJpaEntity)
                        .where(legacyOrderIdMappingJpaEntity.legacyOrderId.eq(legacyOrderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * legacyOrderId에 해당하는 매핑 존재 여부 확인.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 존재 여부
     */
    public boolean existsByLegacyOrderId(long legacyOrderId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(legacyOrderIdMappingJpaEntity)
                        .where(legacyOrderIdMappingJpaEntity.legacyOrderId.eq(legacyOrderId))
                        .fetchFirst();
        return result != null;
    }
}
