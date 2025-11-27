package com.ryuqq.marketplace.adapter.out.persistence.category.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CategoryQueryDslRepository - Category QueryDSL Repository
 *
 * <p>Note: Entity 직접 반환 (Adapter에서 Domain 변환)</p>
 */
@Repository
public class CategoryQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    public CategoryQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<CategoryJpaEntity> findByParentId(Long parentId) {
        return queryFactory
            .selectFrom(category)
            .where(parentIdEq(parentId))
            .orderBy(category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findAllActiveVisible() {
        return queryFactory
            .selectFrom(category)
            .where(
                category.status.eq(CategoryStatus.ACTIVE),
                category.isVisible.isTrue()
            )
            .orderBy(category.depth.asc(), category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findAll() {
        return queryFactory
            .selectFrom(category)
            .orderBy(category.depth.asc(), category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findListableLeaves(CategorySearchQuery query) {
        return queryFactory
            .selectFrom(category)
            .where(
                category.isLeaf.isTrue(),
                category.isListable.isTrue(),
                category.status.eq(CategoryStatus.ACTIVE),
                departmentEq(query.department()),
                productGroupEq(query.productGroup()),
                genderScopeEq(query.genderScope())
            )
            .orderBy(category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findDescendants(Long categoryId) {
        // 먼저 해당 카테고리의 path를 조회
        CategoryJpaEntity parent = queryFactory
            .selectFrom(category)
            .where(category.id.eq(categoryId))
            .fetchOne();

        if (parent == null) {
            return List.of();
        }

        String parentPath = parent.getPath();

        return queryFactory
            .selectFrom(category)
            .where(
                category.path.startsWith(parentPath + "/"),
                category.id.ne(categoryId)
            )
            .orderBy(category.depth.asc(), category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findAncestors(Long categoryId) {
        // 먼저 해당 카테고리의 path를 조회
        CategoryJpaEntity target = queryFactory
            .selectFrom(category)
            .where(category.id.eq(categoryId))
            .fetchOne();

        if (target == null || target.getPath() == null) {
            return List.of();
        }

        // path에서 조상 ID들 추출 (e.g., "1/2/3" -> [1, 2, 3])
        String[] pathParts = target.getPath().split("/");
        List<Long> ancestorIds = java.util.Arrays.stream(pathParts)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .toList();

        if (ancestorIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
            .selectFrom(category)
            .where(category.id.in(ancestorIds))
            .orderBy(category.depth.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return queryFactory
            .selectFrom(category)
            .where(
                category.nameKo.containsIgnoreCase(keyword)
                    .or(category.nameEn.containsIgnoreCase(keyword))
                    .or(category.code.containsIgnoreCase(keyword))
                    .or(category.displayName.containsIgnoreCase(keyword))
            )
            .orderBy(category.depth.asc(), category.sortOrder.asc())
            .fetch();
    }

    public List<CategoryJpaEntity> findUpdatedSince(LocalDateTime since) {
        return queryFactory
            .selectFrom(category)
            .where(category.updatedAt.goe(since))
            .orderBy(category.updatedAt.asc())
            .fetch();
    }

    public boolean hasChildren(Long categoryId) {
        Long count = queryFactory
            .select(category.count())
            .from(category)
            .where(category.parentId.eq(categoryId))
            .fetchOne();

        return count != null && count > 0;
    }

    // BooleanExpression helpers
    private BooleanExpression parentIdEq(Long parentId) {
        return parentId == null
            ? category.parentId.isNull()
            : category.parentId.eq(parentId);
    }

    private BooleanExpression departmentEq(String department) {
        return department == null
            ? null
            : category.department.stringValue().eq(department);
    }

    private BooleanExpression productGroupEq(String productGroup) {
        return productGroup == null
            ? null
            : category.productGroup.stringValue().eq(productGroup);
    }

    private BooleanExpression genderScopeEq(String genderScope) {
        return genderScope == null
            ? null
            : category.genderScope.stringValue().eq(genderScope);
    }
}
