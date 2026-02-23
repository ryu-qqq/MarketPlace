package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceCode;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import org.springframework.stereotype.Component;

/** InboundSource JPA Entity ↔ ExternalSource Domain Mapper. */
@Component
public class InboundSourceJpaEntityMapper {

    public InboundSourceJpaEntity toEntity(ExternalSource source) {
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

    public ExternalSource toDomain(InboundSourceJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = ExternalSourceId.of(entity.getId());
        return ExternalSource.reconstitute(
                id,
                ExternalSourceCode.of(entity.getCode()),
                entity.getName(),
                ExternalSourceType.fromString(entity.getType()),
                ExternalSourceStatus.fromString(entity.getStatus()),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
