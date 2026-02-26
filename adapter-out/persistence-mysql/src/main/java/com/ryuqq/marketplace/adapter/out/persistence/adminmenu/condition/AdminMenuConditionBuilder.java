package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.QAdminMenuJpaEntity.adminMenuJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/**
 * AdminMenuConditionBuilder - Admin 메뉴 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class AdminMenuConditionBuilder {

    /**
     * active = true 조건.
     *
     * @return active 일치 조건
     */
    public BooleanExpression activeOnly() {
        return adminMenuJpaEntity.active.isTrue();
    }

    /**
     * required_role_level <= roleLevel 조건.
     *
     * @param roleLevel 최대 역할 레벨
     * @return 역할 레벨 조건
     */
    public BooleanExpression roleLevelLoe(int roleLevel) {
        return adminMenuJpaEntity.requiredRoleLevel.loe(roleLevel);
    }

    /**
     * parent_id NULLS FIRST, display_order ASC 정렬.
     *
     * @return 정렬 조건 배열
     */
    public OrderSpecifier<?>[] orderByParentAndDisplayOrder() {
        return new OrderSpecifier<?>[] {
            adminMenuJpaEntity.parentId.asc().nullsFirst(), adminMenuJpaEntity.displayOrder.asc()
        };
    }
}
