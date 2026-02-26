package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite.CategoryMappingWithCategoryDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.QCategoryMappingJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/** CategoryMapping QueryDSL Repository. */
@Repository
public class CategoryMappingQueryDslRepository {

    private static final QCategoryMappingJpaEntity categoryMapping =
            QCategoryMappingJpaEntity.categoryMappingJpaEntity;
    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    private final JPAQueryFactory queryFactory;

    public CategoryMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<CategoryMappingWithCategoryDto> findMappedCategoriesByPresetId(Long presetId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryMappingWithCategoryDto.class,
                                categoryMapping.id,
                                categoryMapping.internalCategoryId,
                                category.nameKo,
                                category.displayPath,
                                category.code))
                .from(categoryMapping)
                .join(category)
                .on(categoryMapping.internalCategoryId.eq(category.id))
                .where(categoryMapping.presetId.eq(presetId), categoryMapping.status.eq("ACTIVE"))
                .fetch();
    }
}
