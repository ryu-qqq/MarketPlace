package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeJpaEntityMapper - 공통 코드 타입 Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeTypeJpaEntityMapper {

    /**
     * Domain → Entity 변환.
     *
     * @param domain CommonCodeType 도메인 객체
     * @return CommonCodeTypeJpaEntity
     */
    public CommonCodeTypeJpaEntity toEntity(CommonCodeType domain) {
        return CommonCodeTypeJpaEntity.create(
                domain.idValue(),
                domain.code(),
                domain.name(),
                domain.description(),
                domain.displayOrder(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * Entity → Domain 변환.
     *
     * @param entity CommonCodeTypeJpaEntity
     * @return CommonCodeType 도메인 객체
     */
    public CommonCodeType toDomain(CommonCodeTypeJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? CommonCodeTypeId.of(entity.getId())
                        : CommonCodeTypeId.forNew();
        return CommonCodeType.reconstitute(
                id,
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.isActive(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
