package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueName;
import org.springframework.stereotype.Component;

/** CanonicalOptionValue JPA Entity Mapper. */
@Component
public class CanonicalOptionValueJpaEntityMapper {

    public CanonicalOptionValue toDomain(CanonicalOptionValueJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        return CanonicalOptionValue.reconstitute(
                CanonicalOptionValueId.of(entity.getId()),
                CanonicalOptionValueCode.of(entity.getCode()),
                CanonicalOptionValueName.of(entity.getNameKo(), entity.getNameEn()),
                entity.getSortOrder());
    }
}
