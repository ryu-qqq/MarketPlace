package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceCode;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import org.springframework.stereotype.Component;

/** InboundSource JPA Entity <-> InboundSource Domain Mapper. */
@Component
public class InboundSourceJpaEntityMapper {

    public InboundSourceJpaEntity toEntity(InboundSource source) {
        return InboundSourceJpaEntity.create(
                source.idValue(),
                source.codeValue(),
                source.name(),
                source.type().name(),
                source.status().name(),
                source.description(),
                source.createdAt(),
                source.updatedAt());
    }

    public InboundSource toDomain(InboundSourceJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = InboundSourceId.of(entity.getId());
        return InboundSource.reconstitute(
                id,
                InboundSourceCode.of(entity.getCode()),
                entity.getName(),
                InboundSourceType.fromString(entity.getType()),
                InboundSourceStatus.fromString(entity.getStatus()),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
