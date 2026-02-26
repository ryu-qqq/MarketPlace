package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.QLegacyCommonCodeEntity.legacyCommonCodeEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 공통 코드 QueryDSL 조회 Repository.
 *
 * <p>세토프 DB에서 공통 코드를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyCommonCodeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyCommonCodeQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<LegacyCommonCodeEntity> findByCodeGroupId(Long codeGroupId) {
        return queryFactory
                .selectFrom(legacyCommonCodeEntity)
                .where(
                        legacyCommonCodeEntity.codeGroupId.eq(codeGroupId),
                        legacyCommonCodeEntity.deleteYn.eq("N"))
                .orderBy(legacyCommonCodeEntity.displayOrder.asc())
                .fetch();
    }
}
