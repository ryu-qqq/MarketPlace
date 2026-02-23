package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.id.AdminMenuId;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import org.springframework.stereotype.Component;

/**
 * AdminMenuJpaEntityMapper - Admin 메뉴 Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class AdminMenuJpaEntityMapper {

    public AdminMenuJpaEntity toEntity(AdminMenu domain) {
        return AdminMenuJpaEntity.create(
                domain.idValue(),
                domain.parentId(),
                domain.title(),
                domain.url(),
                domain.iconName(),
                domain.displayOrder(),
                domain.requiredRole().level(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public AdminMenu toDomain(AdminMenuJpaEntity entity) {
        AdminMenuId id =
                entity.getId() != null ? AdminMenuId.of(entity.getId()) : AdminMenuId.forNew();
        return AdminMenu.reconstitute(
                id,
                entity.getParentId(),
                entity.getTitle(),
                entity.getUrl(),
                entity.getIconName(),
                entity.getDisplayOrder(),
                AdminRole.fromLevel(entity.getRequiredRoleLevel()),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
