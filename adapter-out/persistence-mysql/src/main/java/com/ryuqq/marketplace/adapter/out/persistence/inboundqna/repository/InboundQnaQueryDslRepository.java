package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.QInboundQnaJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** InboundQna QueryDSL Repository. */
@Repository
public class InboundQnaQueryDslRepository {

    private static final QInboundQnaJpaEntity Q = QInboundQnaJpaEntity.inboundQnaJpaEntity;

    private final JPAQueryFactory queryFactory;

    public InboundQnaQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<InboundQnaJpaEntity> findById(long id) {
        InboundQnaJpaEntity result = queryFactory.selectFrom(Q).where(Q.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    public boolean existsBySalesChannelIdAndExternalQnaId(
            long salesChannelId, String externalQnaId) {
        return queryFactory
                        .selectOne()
                        .from(Q)
                        .where(
                                Q.salesChannelId.eq(salesChannelId),
                                Q.externalQnaId.eq(externalQnaId))
                        .fetchFirst()
                != null;
    }

    public List<InboundQnaJpaEntity> findByStatusOrderByIdAsc(
            InboundQnaJpaEntity.Status status, int limit) {
        return queryFactory
                .selectFrom(Q)
                .where(Q.status.eq(status))
                .orderBy(Q.id.asc())
                .limit(limit)
                .fetch();
    }
}
