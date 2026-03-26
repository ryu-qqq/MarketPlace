package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.QLegacyQnaAnswerEntity.legacyQnaAnswerEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.QLegacyQnaEntity.legacyQnaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.QLegacyQnaImageEntity.legacyQnaImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.QLegacyQnaOrderEntity.legacyQnaOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.QLegacyQnaProductEntity.legacyQnaProductEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.dto.LegacyQnaCompositeQueryDto;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 QnA QueryDSL Repository.
 *
 * <p>luxurydb의 qna, qna_product, qna_order, qna_answer, qna_image 테이블을 조회합니다.
 */
@Repository
public class LegacyQnaQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyQnaQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /** QnA ID로 복합 flat DTO 단건 조회 (qna + qna_product LEFT JOIN + qna_order LEFT JOIN). */
    public Optional<LegacyQnaCompositeQueryDto> fetchQnaComposite(long qnaId) {
        LegacyQnaCompositeQueryDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        LegacyQnaCompositeQueryDto.class,
                                        legacyQnaEntity.id,
                                        legacyQnaEntity.title,
                                        legacyQnaEntity.content,
                                        legacyQnaEntity.privateYn,
                                        legacyQnaEntity.qnaStatus,
                                        legacyQnaEntity.qnaType,
                                        legacyQnaEntity.qnaDetailType,
                                        legacyQnaEntity.userId,
                                        legacyQnaEntity.sellerId,
                                        legacyQnaEntity.userType,
                                        legacyQnaEntity.insertDate,
                                        legacyQnaEntity.updateDate,
                                        legacyQnaProductEntity.productGroupId,
                                        legacyQnaOrderEntity.orderId))
                        .from(legacyQnaEntity)
                        .leftJoin(legacyQnaProductEntity)
                        .on(legacyQnaProductEntity.qnaId.eq(legacyQnaEntity.id))
                        .leftJoin(legacyQnaOrderEntity)
                        .on(legacyQnaOrderEntity.qnaId.eq(legacyQnaEntity.id))
                        .where(legacyQnaEntity.id.eq(qnaId), legacyQnaEntity.deleteYn.eq("N"))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /** 검색 조건 기반 QnA 목록 조회 (커서 기반 페이징). */
    public List<LegacyQnaCompositeQueryDto> fetchQnaList(LegacyQnaSearchParams params) {
        BooleanBuilder where = buildWhereCondition(params);

        return queryFactory
                .select(
                        Projections.constructor(
                                LegacyQnaCompositeQueryDto.class,
                                legacyQnaEntity.id,
                                legacyQnaEntity.title,
                                legacyQnaEntity.content,
                                legacyQnaEntity.privateYn,
                                legacyQnaEntity.qnaStatus,
                                legacyQnaEntity.qnaType,
                                legacyQnaEntity.qnaDetailType,
                                legacyQnaEntity.userId,
                                legacyQnaEntity.sellerId,
                                legacyQnaEntity.userType,
                                legacyQnaEntity.insertDate,
                                legacyQnaEntity.updateDate,
                                legacyQnaProductEntity.productGroupId,
                                legacyQnaOrderEntity.orderId))
                .from(legacyQnaEntity)
                .leftJoin(legacyQnaProductEntity)
                .on(legacyQnaProductEntity.qnaId.eq(legacyQnaEntity.id))
                .leftJoin(legacyQnaOrderEntity)
                .on(legacyQnaOrderEntity.qnaId.eq(legacyQnaEntity.id))
                .where(where)
                .orderBy(legacyQnaEntity.id.desc())
                .limit(params.size())
                .fetch();
    }

    /** 검색 조건 기반 QnA 카운트 조회. */
    public long countQnas(LegacyQnaSearchParams params) {
        BooleanBuilder where = buildWhereCondition(params);

        Long count =
                queryFactory
                        .select(legacyQnaEntity.count())
                        .from(legacyQnaEntity)
                        .where(where)
                        .fetchOne();

        return count != null ? count : 0L;
    }

    /** QnA ID 목록에 해당하는 답변 조회. */
    public List<
                    com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity
                            .LegacyQnaAnswerEntity>
            fetchAnswersByQnaId(long qnaId) {
        return queryFactory
                .selectFrom(legacyQnaAnswerEntity)
                .where(
                        legacyQnaAnswerEntity.qnaId.eq(qnaId),
                        legacyQnaAnswerEntity.deleteYn.eq("N"))
                .orderBy(legacyQnaAnswerEntity.id.asc())
                .fetch();
    }

    /** QnA ID에 해당하는 이미지 조회 (질문 이미지). */
    public List<
                    com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity
                            .LegacyQnaImageEntity>
            fetchImagesByQnaId(long qnaId) {
        return queryFactory
                .selectFrom(legacyQnaImageEntity)
                .where(legacyQnaImageEntity.qnaId.eq(qnaId), legacyQnaImageEntity.deleteYn.eq("N"))
                .orderBy(legacyQnaImageEntity.displayOrder.asc())
                .fetch();
    }

    /** 답변 ID에 해당하는 이미지 조회 (답변 이미지). */
    public List<
                    com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity
                            .LegacyQnaImageEntity>
            fetchImagesByAnswerId(long answerId) {
        return queryFactory
                .selectFrom(legacyQnaImageEntity)
                .where(
                        legacyQnaImageEntity.qnaAnswerId.eq(answerId),
                        legacyQnaImageEntity.deleteYn.eq("N"))
                .orderBy(legacyQnaImageEntity.displayOrder.asc())
                .fetch();
    }

    private BooleanBuilder buildWhereCondition(LegacyQnaSearchParams params) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(legacyQnaEntity.deleteYn.eq("N"));

        if (params.qnaStatus() != null && !params.qnaStatus().isBlank()) {
            where.and(legacyQnaEntity.qnaStatus.eq(params.qnaStatus()));
        }
        if (params.qnaType() != null && !params.qnaType().isBlank()) {
            where.and(legacyQnaEntity.qnaType.eq(params.qnaType()));
        }
        if (params.qnaDetailType() != null && !params.qnaDetailType().isBlank()) {
            where.and(legacyQnaEntity.qnaDetailType.eq(params.qnaDetailType()));
        }
        if (params.privateYn() != null && !params.privateYn().isBlank()) {
            where.and(legacyQnaEntity.privateYn.eq(params.privateYn()));
        }
        if (params.sellerId() != null) {
            where.and(legacyQnaEntity.sellerId.eq(params.sellerId()));
        }
        if (params.lastDomainId() != null && params.lastDomainId() > 0) {
            where.and(legacyQnaEntity.id.lt(params.lastDomainId()));
        }
        if (params.startDate() != null) {
            where.and(legacyQnaEntity.insertDate.goe(params.startDate()));
        }
        if (params.endDate() != null) {
            where.and(legacyQnaEntity.insertDate.loe(params.endDate()));
        }
        if (params.searchKeyword() != null && !params.searchKeyword().isBlank()) {
            where.and(
                    legacyQnaEntity
                            .title
                            .containsIgnoreCase(params.searchKeyword())
                            .or(
                                    legacyQnaEntity.content.containsIgnoreCase(
                                            params.searchKeyword())));
        }

        return where;
    }
}
