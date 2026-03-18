package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.QClaimHistoryJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 클레임 이력 QueryDSL Repository. */
@Repository
public class ClaimHistoryQueryDslRepository {

    private static final QClaimHistoryJpaEntity claimHistory =
            QClaimHistoryJpaEntity.claimHistoryJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ClaimHistoryQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ClaimHistoryJpaEntity> findByClaimTypeAndClaimId(String claimType, String claimId) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(
                        claimHistory.claimType.eq(claimType),
                        claimHistory.claimId.eq(claimId))
                .orderBy(claimHistory.createdAt.asc())
                .fetch();
    }

    public List<ClaimHistoryJpaEntity> findByClaimTypeAndClaimIds(
            String claimType, List<String> claimIds) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(
                        claimHistory.claimType.eq(claimType),
                        claimHistory.claimId.in(claimIds))
                .orderBy(claimHistory.createdAt.asc())
                .fetch();
    }
}
