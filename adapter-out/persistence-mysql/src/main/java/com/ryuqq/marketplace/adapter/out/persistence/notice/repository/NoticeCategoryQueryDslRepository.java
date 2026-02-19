package com.ryuqq.marketplace.adapter.out.persistence.notice.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.notice.condition.NoticeCategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.QNoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.QNoticeFieldJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** NoticeCategory QueryDSL Repository. */
@Repository
public class NoticeCategoryQueryDslRepository {

    private static final QNoticeCategoryJpaEntity noticeCategory =
            QNoticeCategoryJpaEntity.noticeCategoryJpaEntity;
    private static final QNoticeFieldJpaEntity noticeField =
            QNoticeFieldJpaEntity.noticeFieldJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final NoticeCategoryConditionBuilder conditionBuilder;

    public NoticeCategoryQueryDslRepository(
            JPAQueryFactory queryFactory, NoticeCategoryConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<NoticeCategoryJpaEntity> findById(Long id) {
        NoticeCategoryJpaEntity entity =
                queryFactory.selectFrom(noticeCategory).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<NoticeCategoryJpaEntity> findByTargetCategoryGroup(String targetCategoryGroup) {
        NoticeCategoryJpaEntity entity =
                queryFactory
                        .selectFrom(noticeCategory)
                        .where(conditionBuilder.targetCategoryGroupEq(targetCategoryGroup))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<NoticeCategoryJpaEntity> findByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryFactory
                .selectFrom(noticeCategory)
                .where(
                        conditionBuilder.activeEq(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(NoticeCategorySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(noticeCategory.count())
                        .from(noticeCategory)
                        .where(
                                conditionBuilder.activeEq(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public List<NoticeFieldJpaEntity> findFieldsByCategoryId(Long categoryId) {
        return queryFactory
                .selectFrom(noticeField)
                .where(noticeField.noticeCategoryId.eq(categoryId))
                .orderBy(noticeField.sortOrder.asc())
                .fetch();
    }

    public List<NoticeFieldJpaEntity> findFieldsByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(noticeField)
                .where(noticeField.noticeCategoryId.in(categoryIds))
                .orderBy(noticeField.sortOrder.asc())
                .fetch();
    }

    private OrderSpecifier<?> resolveOrderSpecifier(NoticeCategorySearchCriteria criteria) {
        NoticeCategorySortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc ? noticeCategory.createdAt.asc() : noticeCategory.createdAt.desc();
            case CODE -> isAsc ? noticeCategory.code.asc() : noticeCategory.code.desc();
        };
    }
}
