package com.ryuqq.marketplace.adapter.out.persistence.qna.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Qna QueryDSL Repository. */
@Repository
public class QnaQueryDslRepository {

    private static final QQnaJpaEntity Q = QQnaJpaEntity.qnaJpaEntity;

    private final JPAQueryFactory queryFactory;

    public QnaQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<QnaJpaEntity> findById(long id) {
        QnaJpaEntity result = queryFactory
                .selectFrom(Q)
                .where(Q.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<QnaJpaEntity> findBySellerIdAndStatus(
            long sellerId, QnaJpaEntity.Status status, int offset, int limit) {
        var query = queryFactory
                .selectFrom(Q)
                .where(Q.sellerId.eq(sellerId));

        if (status != null) {
            query.where(Q.status.eq(status));
        }

        return query
                .orderBy(Q.id.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public long countBySellerIdAndStatus(long sellerId, QnaJpaEntity.Status status) {
        var query = queryFactory
                .select(Q.count())
                .from(Q)
                .where(Q.sellerId.eq(sellerId));

        if (status != null) {
            query.where(Q.status.eq(status));
        }

        Long count = query.fetchOne();
        return count != null ? count : 0L;
    }

    public List<QnaJpaEntity> search(QnaSearchCondition condition) {
        BooleanBuilder where = buildSearchCondition(condition);

        return queryFactory
                .selectFrom(Q)
                .where(where)
                .orderBy(Q.id.desc())
                .limit(condition.size())
                .fetch();
    }

    public long countByCondition(QnaSearchCondition condition) {
        BooleanBuilder where = buildSearchCondition(condition);

        // cursorId는 카운트에서 제외
        BooleanBuilder countWhere = new BooleanBuilder();
        if (condition.sellerId() != null) {
            countWhere.and(Q.sellerId.eq(condition.sellerId()));
        }
        if (condition.status() != null) {
            countWhere.and(Q.status.eq(QnaJpaEntity.Status.valueOf(condition.status().name())));
        }
        if (condition.qnaType() != null) {
            countWhere.and(Q.qnaType.eq(condition.qnaType().name()));
        }
        if (condition.keyword() != null && !condition.keyword().isBlank()) {
            countWhere.and(Q.questionContent.contains(condition.keyword())
                    .or(Q.questionTitle.contains(condition.keyword())));
        }
        if (condition.fromDate() != null) {
            countWhere.and(Q.createdAt.goe(condition.fromDate()));
        }
        if (condition.toDate() != null) {
            countWhere.and(Q.createdAt.loe(condition.toDate()));
        }

        Long count = queryFactory.select(Q.count()).from(Q).where(countWhere).fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanBuilder buildSearchCondition(QnaSearchCondition condition) {
        BooleanBuilder where = new BooleanBuilder();

        if (condition.sellerId() != null) {
            where.and(Q.sellerId.eq(condition.sellerId()));
        }
        if (condition.status() != null) {
            where.and(Q.status.eq(QnaJpaEntity.Status.valueOf(condition.status().name())));
        }
        if (condition.qnaType() != null) {
            where.and(Q.qnaType.eq(condition.qnaType().name()));
        }
        if (condition.keyword() != null && !condition.keyword().isBlank()) {
            where.and(Q.questionContent.contains(condition.keyword())
                    .or(Q.questionTitle.contains(condition.keyword())));
        }
        if (condition.fromDate() != null) {
            where.and(Q.createdAt.goe(condition.fromDate()));
        }
        if (condition.toDate() != null) {
            where.and(Q.createdAt.loe(condition.toDate()));
        }
        if (condition.cursorId() != null) {
            where.and(Q.id.lt(condition.cursorId()));
        }

        return where;
    }
}
