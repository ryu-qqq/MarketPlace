package com.ryuqq.marketplace.adapter.out.persistence.claimsync.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.claimsync.entity.QClaimSyncLogJpaEntity;
import org.springframework.stereotype.Repository;

/** 클레임 동기화 로그 QueryDSL Repository. */
@Repository
public class ClaimSyncLogQueryDslRepository {

    private static final QClaimSyncLogJpaEntity syncLog =
            QClaimSyncLogJpaEntity.claimSyncLogJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ClaimSyncLogQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public boolean exists(
            long salesChannelId,
            String externalProductOrderId,
            String claimType,
            String claimStatus) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(syncLog)
                        .where(
                                syncLog.salesChannelId.eq(salesChannelId),
                                syncLog.externalProductOrderId.eq(externalProductOrderId),
                                syncLog.externalClaimType.eq(claimType),
                                syncLog.externalClaimStatus.eq(claimStatus))
                        .fetchFirst();
        return result != null;
    }
}
