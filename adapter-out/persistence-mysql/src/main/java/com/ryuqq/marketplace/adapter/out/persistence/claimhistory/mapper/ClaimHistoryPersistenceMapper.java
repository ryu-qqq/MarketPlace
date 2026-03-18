package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.id.ClaimHistoryId;
import com.ryuqq.marketplace.domain.claimhistory.vo.Actor;
import com.ryuqq.marketplace.domain.claimhistory.vo.ActorType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.springframework.stereotype.Component;

/**
 * ClaimHistory JPA Entity Mapper.
 *
 * <p>도메인 객체와 JPA 엔티티 간의 변환을 담당합니다.
 */
@Component
public class ClaimHistoryPersistenceMapper {

    public ClaimHistoryJpaEntity toEntity(ClaimHistory domain) {
        return ClaimHistoryJpaEntity.create(
                domain.idValue(),
                domain.claimType().name(),
                domain.claimId(),
                domain.historyType().name(),
                domain.title(),
                domain.message(),
                domain.actor().actorType().name(),
                domain.actor().actorId(),
                domain.actor().actorName(),
                domain.createdAt());
    }

    public ClaimHistory toDomain(ClaimHistoryJpaEntity entity) {
        Actor actor = new Actor(
                ActorType.valueOf(entity.getActorType()),
                entity.getActorId(),
                entity.getActorName());

        return ClaimHistory.reconstitute(
                ClaimHistoryId.of(entity.getId()),
                ClaimType.valueOf(entity.getClaimType()),
                entity.getClaimId(),
                ClaimHistoryType.valueOf(entity.getHistoryType()),
                entity.getTitle(),
                entity.getMessage(),
                actor,
                entity.getCreatedAt());
    }
}
