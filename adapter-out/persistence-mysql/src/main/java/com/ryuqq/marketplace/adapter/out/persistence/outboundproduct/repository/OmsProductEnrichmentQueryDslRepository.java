package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity.productJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.QProductGroupImageJpaEntity.productGroupImageJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition.OmsProductEnrichmentConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * OMS 상품 목록 enrichment 전용 QueryDSL 레포지토리.
 *
 * <p>대표 이미지, 가격/재고, 연동상태를 productGroupId 기준으로 조회한다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class OmsProductEnrichmentQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OmsProductEnrichmentConditionBuilder conditionBuilder;

    public OmsProductEnrichmentQueryDslRepository(
            JPAQueryFactory queryFactory, OmsProductEnrichmentConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 상품그룹별 대표(THUMBNAIL) 이미지 URL 조회.
     *
     * @param pgIds 상품그룹 ID 목록
     * @return productGroupId -> 대표 이미지 DTO
     */
    public Map<Long, OmsProductMainImageDto> fetchMainImages(List<Long> pgIds) {
        List<OmsProductMainImageDto> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        OmsProductMainImageDto.class,
                                        productGroupImageJpaEntity.productGroupId,
                                        productGroupImageJpaEntity.originUrl))
                        .from(productGroupImageJpaEntity)
                        .where(
                                conditionBuilder.imageProductGroupIdIn(pgIds),
                                conditionBuilder.imageThumbnailType(),
                                conditionBuilder.imageNotDeleted())
                        .fetch();

        return results.stream()
                .collect(
                        Collectors.toMap(
                                OmsProductMainImageDto::productGroupId,
                                Function.identity(),
                                (first, second) -> first));
    }

    /**
     * 상품그룹별 최저가격/총재고 조회.
     *
     * @param pgIds 상품그룹 ID 목록
     * @return productGroupId -> 가격/재고 DTO
     */
    public Map<Long, OmsProductPriceStockDto> fetchPriceStock(List<Long> pgIds) {
        List<OmsProductPriceStockDto> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        OmsProductPriceStockDto.class,
                                        productJpaEntity.productGroupId,
                                        productJpaEntity.currentPrice.min(),
                                        productJpaEntity.stockQuantity.sum()))
                        .from(productJpaEntity)
                        .where(conditionBuilder.productProductGroupIdIn(pgIds))
                        .groupBy(productJpaEntity.productGroupId)
                        .fetch();

        return results.stream()
                .collect(
                        Collectors.toMap(
                                OmsProductPriceStockDto::productGroupId, Function.identity()));
    }

    /**
     * (상품그룹 + 샵)별 최신 연동상태 조회.
     *
     * <p>서브쿼리로 (productGroupId, shopId)별 max(processedAt)을 구한 뒤, 해당 행의 status를 매칭한다.
     *
     * @param pgIds 상품그룹 ID 목록
     * @return compositeKey(productGroupId_shopId) -> 연동상태 DTO
     */
    public Map<String, OmsProductSyncInfoDto> fetchLatestSyncInfo(List<Long> pgIds) {
        QOutboundSyncOutboxJpaEntity sub = new QOutboundSyncOutboxJpaEntity("sub");

        List<OmsProductSyncInfoDto> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        OmsProductSyncInfoDto.class,
                                        outboundSyncOutboxJpaEntity.productGroupId,
                                        outboundSyncOutboxJpaEntity.shopId,
                                        outboundSyncOutboxJpaEntity.status.stringValue(),
                                        outboundSyncOutboxJpaEntity.processedAt))
                        .from(outboundSyncOutboxJpaEntity)
                        .where(
                                conditionBuilder.syncProductGroupIdIn(pgIds),
                                outboundSyncOutboxJpaEntity.processedAt.eq(
                                        JPAExpressions.select(sub.processedAt.max())
                                                .from(sub)
                                                .where(
                                                        sub.productGroupId.eq(
                                                                outboundSyncOutboxJpaEntity
                                                                        .productGroupId),
                                                        sub.shopId.eq(
                                                                outboundSyncOutboxJpaEntity
                                                                        .shopId))))
                        .fetch();

        return results.stream()
                .collect(
                        Collectors.toMap(
                                OmsProductSyncInfoDto::compositeKey,
                                Function.identity(),
                                (first, second) -> first));
    }
}
