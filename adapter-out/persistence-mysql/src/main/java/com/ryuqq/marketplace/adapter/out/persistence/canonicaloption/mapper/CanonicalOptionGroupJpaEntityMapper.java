package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import java.util.List;
import org.springframework.stereotype.Component;

/** CanonicalOptionGroup JPA Entity Mapper. */
@Component
public class CanonicalOptionGroupJpaEntityMapper {

    public CanonicalOptionGroup toDomain(CanonicalOptionGroupJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = CanonicalOptionGroupId.of(entity.getId());
        var code = CanonicalOptionGroupCode.of(entity.getCode());
        var name = CanonicalOptionGroupName.of(entity.getNameKo(), entity.getNameEn());

        return CanonicalOptionGroup.reconstitute(
                id, code, name, entity.isActive(),
                List.of(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
