package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.QAdminMenuJpaEntity.adminMenuJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.condition.AdminMenuConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * AdminMenuQueryDslRepository - Admin 메뉴 QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class AdminMenuQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final AdminMenuConditionBuilder conditionBuilder;

    public AdminMenuQueryDslRepository(
            JPAQueryFactory queryFactory, AdminMenuConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 활성 메뉴 중 역할 레벨 이하의 메뉴 조회.
     *
     * <p>WHERE active = 1 AND required_role_level <= :roleLevel ORDER BY parent_id NULLS FIRST,
     * display_order ASC
     *
     * @param roleLevel 최대 역할 레벨
     * @return 메뉴 엔티티 목록
     */
    public List<AdminMenuJpaEntity> findActiveByMaxRoleLevel(int roleLevel) {
        return queryFactory
                .selectFrom(adminMenuJpaEntity)
                .where(conditionBuilder.activeOnly(), conditionBuilder.roleLevelLoe(roleLevel))
                .orderBy(conditionBuilder.orderByParentAndDisplayOrder())
                .fetch();
    }
}
