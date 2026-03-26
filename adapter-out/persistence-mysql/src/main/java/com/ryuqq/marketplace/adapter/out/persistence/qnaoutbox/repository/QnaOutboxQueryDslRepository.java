package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QQnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** QnaOutbox QueryDSL Repository. */
@Repository
public class QnaOutboxQueryDslRepository {

    private static final QQnaOutboxJpaEntity Q = QQnaOutboxJpaEntity.qnaOutboxJpaEntity;

    private final JPAQueryFactory queryFactory;

    public QnaOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<QnaOutboxJpaEntity> findById(long id) {
        QnaOutboxJpaEntity result = queryFactory
                .selectFrom(Q)
                .where(Q.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<QnaOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(Q)
                .where(
                        Q.status.eq(QnaOutboxJpaEntity.Status.PENDING),
                        Q.createdAt.before(beforeTime))
                .orderBy(Q.id.asc())
                .limit(limit)
                .fetch();
    }

    public List<QnaOutboxJpaEntity> findProcessingTimeoutOutboxes(Instant timeoutBefore, int limit) {
        return queryFactory
                .selectFrom(Q)
                .where(
                        Q.status.eq(QnaOutboxJpaEntity.Status.PROCESSING),
                        Q.updatedAt.before(timeoutBefore))
                .orderBy(Q.id.asc())
                .limit(limit)
                .fetch();
    }
}
