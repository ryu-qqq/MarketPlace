package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QDescriptionImageJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * DescriptionImage QueryDSL Repository.
 *
 * <p>PER-ADP-001: QueryAdapter 전용 QueryDSL 레포지토리입니다.
 */
@Repository
public class DescriptionImageQueryDslRepository {

    private static final QDescriptionImageJpaEntity descriptionImage =
            QDescriptionImageJpaEntity.descriptionImageJpaEntity;

    private final JPAQueryFactory queryFactory;

    public DescriptionImageQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<DescriptionImageJpaEntity> findById(Long id) {
        DescriptionImageJpaEntity entity =
                queryFactory
                        .selectFrom(descriptionImage)
                        .where(descriptionImage.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }
}
