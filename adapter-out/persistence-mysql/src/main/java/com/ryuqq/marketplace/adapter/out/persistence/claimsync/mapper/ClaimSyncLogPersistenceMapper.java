package com.ryuqq.marketplace.adapter.out.persistence.claimsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.claimsync.entity.ClaimSyncLogJpaEntity;
import com.ryuqq.marketplace.domain.claimsync.aggregate.ClaimSyncLog;
import com.ryuqq.marketplace.domain.claimsync.id.ClaimSyncLogId;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** 클레임 동기화 로그 Persistence Mapper. */
@Component
public class ClaimSyncLogPersistenceMapper {

    public ClaimSyncLogJpaEntity toEntity(ClaimSyncLog syncLog) {
        return ClaimSyncLogJpaEntity.create(
                null,
                syncLog.salesChannelId(),
                syncLog.externalProductOrderId(),
                syncLog.externalClaimType(),
                syncLog.externalClaimStatus(),
                syncLog.internalClaimType(),
                syncLog.internalClaimId(),
                syncLog.action().name(),
                Instant.now());
    }

    public ClaimSyncLog toDomain(ClaimSyncLogJpaEntity entity) {
        return ClaimSyncLog.reconstitute(
                ClaimSyncLogId.of(entity.getId()),
                entity.getSalesChannelId(),
                entity.getExternalProductOrderId(),
                entity.getExternalClaimType(),
                entity.getExternalClaimStatus(),
                entity.getInternalClaimType(),
                entity.getInternalClaimId(),
                ClaimSyncAction.valueOf(entity.getAction()),
                entity.getSyncedAt());
    }
}
