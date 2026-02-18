package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import java.util.List;
import org.springframework.stereotype.Component;

/** CanonicalOptionGroup JPA Entity Mapper. */
@Component
public class CanonicalOptionGroupJpaEntityMapper {

    private final CanonicalOptionValueJpaEntityMapper valueMapper;

    public CanonicalOptionGroupJpaEntityMapper(CanonicalOptionValueJpaEntityMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public CanonicalOptionGroup toDomain(
            CanonicalOptionGroupJpaEntity entity,
            List<CanonicalOptionValueJpaEntity> valueEntities) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = CanonicalOptionGroupId.of(entity.getId());
        var code = CanonicalOptionGroupCode.of(entity.getCode());
        var name = CanonicalOptionGroupName.of(entity.getNameKo(), entity.getNameEn());

        List<CanonicalOptionValue> values =
                valueEntities.stream().map(valueMapper::toDomain).toList();

        return CanonicalOptionGroup.reconstitute(
                id,
                code,
                name,
                entity.isActive(),
                values,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
