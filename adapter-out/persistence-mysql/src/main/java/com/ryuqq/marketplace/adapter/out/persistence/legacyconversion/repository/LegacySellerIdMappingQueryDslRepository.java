package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacySellerIdMappingJpaEntity.legacySellerIdMappingJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LegacySellerIdMappingQueryDslRepository - 셀러 ID 매핑 QueryDSL 조회.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class LegacySellerIdMappingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacySellerIdMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 레거시 셀러 ID로 내부 셀러 ID 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 내부 셀러 ID Optional
     */
    public Optional<Long> findInternalSellerIdByLegacySellerId(long legacySellerId) {
        Long internalSellerId =
                queryFactory
                        .select(legacySellerIdMappingJpaEntity.internalSellerId)
                        .from(legacySellerIdMappingJpaEntity)
                        .where(legacySellerIdMappingJpaEntity.legacySellerId.eq(legacySellerId))
                        .fetchOne();
        return Optional.ofNullable(internalSellerId);
    }
}
