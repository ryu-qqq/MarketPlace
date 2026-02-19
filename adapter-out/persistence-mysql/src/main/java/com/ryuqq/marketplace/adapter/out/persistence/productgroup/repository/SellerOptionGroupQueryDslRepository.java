package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * SellerOptionGroup QueryDSL Repository.
 *
 * <p>PER-ADP-001: QueryAdapter 전용 QueryDSL 레포지토리입니다.
 */
@Repository
public class SellerOptionGroupQueryDslRepository {

    private static final QSellerOptionGroupJpaEntity optionGroup =
            QSellerOptionGroupJpaEntity.sellerOptionGroupJpaEntity;
    private static final QSellerOptionValueJpaEntity optionValue =
            QSellerOptionValueJpaEntity.sellerOptionValueJpaEntity;

    private final JPAQueryFactory queryFactory;

    public SellerOptionGroupQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /** productGroupId로 활성 SellerOptionGroup 목록 조회. */
    public List<SellerOptionGroupJpaEntity> findByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(optionGroup)
                .where(optionGroup.productGroupId.eq(productGroupId), optionGroup.deleted.isFalse())
                .orderBy(optionGroup.sortOrder.asc())
                .fetch();
    }

    /** groupId 목록으로 활성 SellerOptionValue 배치 조회. */
    public List<SellerOptionValueJpaEntity> findValuesByGroupIds(List<Long> groupIds) {
        if (groupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(optionValue)
                .where(optionValue.sellerOptionGroupId.in(groupIds), optionValue.deleted.isFalse())
                .orderBy(optionValue.sortOrder.asc())
                .fetch();
    }
}
