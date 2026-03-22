package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.QLegacyDescriptionImageEntity.legacyDescriptionImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.QLegacyProductGroupDetailDescriptionEntity.legacyProductGroupDetailDescriptionEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상세설명 QueryDsl Repository.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository만 사용.
 */
@Repository
public class LegacyProductGroupDescriptionQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductGroupDescriptionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<LegacyProductGroupDetailDescriptionEntity> findDescriptionByProductGroupId(
            long productGroupId) {
        LegacyProductGroupDetailDescriptionEntity result =
                queryFactory
                        .selectFrom(legacyProductGroupDetailDescriptionEntity)
                        .where(
                                legacyProductGroupDetailDescriptionEntity.productGroupId.eq(
                                        productGroupId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<LegacyDescriptionImageEntity> findImagesByProductGroupId(long productGroupId) {
        return queryFactory
                .selectFrom(legacyDescriptionImageEntity)
                .where(
                        legacyDescriptionImageEntity.productGroupId.eq(productGroupId),
                        legacyDescriptionImageEntity.deleted.eq(false))
                .orderBy(legacyDescriptionImageEntity.sortOrder.asc())
                .fetch();
    }
}
