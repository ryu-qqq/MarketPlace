package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping JPA Entity Mapper. */
@Component
public class ChannelOptionMappingJpaEntityMapper {

    public ChannelOptionMappingJpaEntity toEntity(ChannelOptionMapping domain) {
        return ChannelOptionMappingJpaEntity.create(
                domain.idValue(),
                domain.salesChannelIdValue(),
                domain.canonicalOptionValueIdValue(),
                domain.externalOptionCodeValue(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public ChannelOptionMapping toDomain(ChannelOptionMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        return ChannelOptionMapping.reconstitute(
                ChannelOptionMappingId.of(entity.getId()),
                SalesChannelId.of(entity.getSalesChannelId()),
                CanonicalOptionValueId.of(entity.getCanonicalOptionValueId()),
                ExternalOptionCode.of(entity.getExternalOptionCode()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
