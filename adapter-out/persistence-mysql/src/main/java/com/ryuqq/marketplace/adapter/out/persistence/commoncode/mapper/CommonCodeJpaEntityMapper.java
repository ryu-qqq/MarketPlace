package com.ryuqq.marketplace.adapter.out.persistence.commoncode.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import org.springframework.stereotype.Component;

/**
 * CommonCodeJpaEntityMapper - 공통 코드 Entity-Domain 매퍼.
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
public class CommonCodeJpaEntityMapper {

    /**
     * Domain → Entity 변환.
     *
     * @param domain CommonCode 도메인 객체
     * @return CommonCodeJpaEntity
     */
    public CommonCodeJpaEntity toEntity(CommonCode domain) {
        return CommonCodeJpaEntity.create(
                domain.idValue(),
                domain.commonCodeTypeIdValue(),
                domain.codeValue(),
                domain.displayNameValue(),
                domain.displayOrderValue(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * Entity → Domain 변환.
     *
     * @param entity CommonCodeJpaEntity
     * @return CommonCode 도메인 객체
     */
    public CommonCode toDomain(CommonCodeJpaEntity entity) {
        var id = entity.getId() != null ? CommonCodeId.of(entity.getId()) : CommonCodeId.forNew();
        return CommonCode.reconstitute(
                id,
                CommonCodeTypeId.of(entity.getCommonCodeTypeId()),
                entity.getCode(),
                entity.getDisplayName(),
                entity.getDisplayOrder(),
                entity.isActive(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
