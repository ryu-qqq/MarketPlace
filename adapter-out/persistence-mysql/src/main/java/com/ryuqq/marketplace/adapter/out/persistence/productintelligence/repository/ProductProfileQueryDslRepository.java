package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.QProductProfileJpaEntity.productProfileJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductProfileQueryDslRepository - 상품 프로파일 QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ProductProfileQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ProductProfileQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 프로파일 단건 조회.
     *
     * @param profileId 프로파일 ID
     * @return 프로파일 엔티티
     */
    public Optional<ProductProfileJpaEntity> findById(Long profileId) {
        ProductProfileJpaEntity entity =
                queryFactory
                        .selectFrom(productProfileJpaEntity)
                        .where(productProfileJpaEntity.id.eq(profileId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 상품그룹 ID로 최신 프로파일 단건 조회 (버전 기준).
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 프로파일
     */
    public Optional<ProductProfileJpaEntity> findLatestByProductGroupId(Long productGroupId) {
        ProductProfileJpaEntity entity =
                queryFactory
                        .selectFrom(productProfileJpaEntity)
                        .where(productProfileJpaEntity.productGroupId.eq(productGroupId))
                        .orderBy(productProfileJpaEntity.profileVersion.desc())
                        .fetchFirst();
        return Optional.ofNullable(entity);
    }

    /**
     * 상품그룹 ID로 활성(만료되지 않은) 최신 프로파일 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 활성 프로파일
     */
    public Optional<ProductProfileJpaEntity> findLatestActiveByProductGroupId(Long productGroupId) {
        ProductProfileJpaEntity entity =
                queryFactory
                        .selectFrom(productProfileJpaEntity)
                        .where(
                                productProfileJpaEntity.productGroupId.eq(productGroupId),
                                productProfileJpaEntity.expiredAt.isNull())
                        .orderBy(productProfileJpaEntity.profileVersion.desc())
                        .fetchFirst();
        return Optional.ofNullable(entity);
    }

    /**
     * 상품그룹 ID로 모든 프로파일 이력 조회 (최신 순).
     *
     * @param productGroupId 상품그룹 ID
     * @return 프로파일 이력 목록
     */
    public List<ProductProfileJpaEntity> findAllByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(productProfileJpaEntity)
                .where(productProfileJpaEntity.productGroupId.eq(productGroupId))
                .orderBy(productProfileJpaEntity.profileVersion.desc())
                .fetch();
    }

    /**
     * 상품그룹 ID로 최신 완료 프로파일 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 COMPLETED 프로파일
     */
    public Optional<ProductProfileJpaEntity> findLatestCompletedByProductGroupId(
            Long productGroupId) {
        ProductProfileJpaEntity entity =
                queryFactory
                        .selectFrom(productProfileJpaEntity)
                        .where(
                                productProfileJpaEntity.productGroupId.eq(productGroupId),
                                productProfileJpaEntity.status.eq(
                                        ProductProfileJpaEntity.Status.COMPLETED))
                        .orderBy(productProfileJpaEntity.profileVersion.desc())
                        .fetchFirst();
        return Optional.ofNullable(entity);
    }

    /**
     * 특정 상태의 프로파일 목록 조회.
     *
     * @param status 조회할 상태
     * @param limit 최대 조회 개수
     * @return 프로파일 목록
     */
    public List<ProductProfileJpaEntity> findByStatus(
            ProductProfileJpaEntity.Status status, int limit) {
        return queryFactory
                .selectFrom(productProfileJpaEntity)
                .where(
                        productProfileJpaEntity.status.eq(status),
                        productProfileJpaEntity.expiredAt.isNull())
                .orderBy(productProfileJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * ANALYZING 상태에서 모든 분석이 완료되었지만 Aggregation 발행이 안 된 프로파일 조회.
     *
     * <p>조건: status=ANALYZING AND completedAnalysisCount >= expectedAnalysisCount AND updatedAt <
     * stuckThreshold (일정 시간 이상 경과)
     *
     * @param stuckThreshold 이 시각 이전에 업데이트된 건만 조회
     * @param limit 최대 조회 개수
     * @return stuck 프로파일 목록
     */
    public List<ProductProfileJpaEntity> findStuckAnalyzingProfiles(
            java.time.Instant stuckThreshold, int limit) {
        return queryFactory
                .selectFrom(productProfileJpaEntity)
                .where(
                        productProfileJpaEntity.status.eq(ProductProfileJpaEntity.Status.ANALYZING),
                        productProfileJpaEntity.completedAnalysisCount.goe(
                                productProfileJpaEntity.expectedAnalysisCount),
                        productProfileJpaEntity.updatedAt.before(stuckThreshold),
                        productProfileJpaEntity.expiredAt.isNull())
                .orderBy(productProfileJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
