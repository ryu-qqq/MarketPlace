package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import org.springframework.stereotype.Component;

/** SalesChannel JPA Entity Mapper. */
@Component
public class SalesChannelJpaEntityMapper {

    public SalesChannelJpaEntity toEntity(SalesChannel salesChannel) {
        return SalesChannelJpaEntity.create(
                salesChannel.idValue(),
                salesChannel.channelName(),
                salesChannel.status().name(),
                salesChannel.createdAt(),
                salesChannel.updatedAt());
    }

    public SalesChannel toDomain(SalesChannelJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = SalesChannelId.of(entity.getId());
        return SalesChannel.reconstitute(
                id,
                entity.getChannelName(),
                SalesChannelStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
