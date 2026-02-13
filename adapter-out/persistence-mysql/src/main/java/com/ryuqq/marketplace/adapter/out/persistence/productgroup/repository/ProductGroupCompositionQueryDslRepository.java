package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition.ProductGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.QRefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity;
import com.ryuqq.marketplace.application.productgroup.dto.composite.OptionGroupSummaryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSortKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ProductGroup Composition QueryDSL Repository. 크로스 도메인 JOIN 조회용. */
@Repository
public class ProductGroupCompositionQueryDslRepository {

    private static final QProductGroupJpaEntity pg = QProductGroupJpaEntity.productGroupJpaEntity;
    private static final QSellerJpaEntity seller = QSellerJpaEntity.sellerJpaEntity;
    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;
    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;
    private static final QProductGroupImageJpaEntity pgImage =
            QProductGroupImageJpaEntity.productGroupImageJpaEntity;
    private static final QProductJpaEntity product = QProductJpaEntity.productJpaEntity;
    private static final QSellerOptionGroupJpaEntity optionGroup =
            QSellerOptionGroupJpaEntity.sellerOptionGroupJpaEntity;
    private static final QSellerOptionValueJpaEntity optionValue =
            QSellerOptionValueJpaEntity.sellerOptionValueJpaEntity;
    private static final QShippingPolicyJpaEntity shippingPolicy =
            QShippingPolicyJpaEntity.shippingPolicyJpaEntity;
    private static final QRefundPolicyJpaEntity refundPolicy =
            QRefundPolicyJpaEntity.refundPolicyJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ProductGroupConditionBuilder conditionBuilder;

    public ProductGroupCompositionQueryDslRepository(
            JPAQueryFactory queryFactory, ProductGroupConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /** 단건 Composite 조회 (목록용 기본 데이터). */
    public Optional<ProductGroupListCompositeResult> findCompositeById(Long productGroupId) {
        Tuple row =
                queryFactory
                        .select(
                                pg.id,
                                pg.sellerId,
                                seller.sellerName,
                                pg.brandId,
                                brand.nameKo,
                                pg.categoryId,
                                category.nameKo,
                                category.displayPath,
                                category.depth,
                                category.department,
                                category.categoryGroup,
                                pg.productGroupName,
                                pg.optionType,
                                pg.status,
                                pg.createdAt,
                                pg.updatedAt)
                        .from(pg)
                        .leftJoin(seller)
                        .on(pg.sellerId.eq(seller.id))
                        .leftJoin(brand)
                        .on(pg.brandId.eq(brand.id))
                        .leftJoin(category)
                        .on(pg.categoryId.eq(category.id))
                        .where(
                                conditionBuilder.idEq(productGroupId),
                                conditionBuilder.statusNotDeleted())
                        .fetchOne();

        if (row == null) {
            return Optional.empty();
        }

        String thumbnailUrl = findThumbnailUrl(productGroupId);
        int productCount = countProducts(productGroupId);

        return Optional.of(
                ProductGroupListCompositeResult.ofBase(
                        row.get(pg.id),
                        row.get(pg.sellerId),
                        row.get(seller.sellerName),
                        row.get(pg.brandId),
                        row.get(brand.nameKo),
                        row.get(pg.categoryId),
                        row.get(category.nameKo),
                        row.get(category.displayPath),
                        safeInt(row.get(category.depth)),
                        row.get(category.department),
                        row.get(category.categoryGroup),
                        row.get(pg.productGroupName),
                        row.get(pg.optionType),
                        row.get(pg.status),
                        thumbnailUrl,
                        productCount,
                        row.get(pg.createdAt),
                        row.get(pg.updatedAt)));
    }

    /** 목록 Composite 조회. */
    public List<ProductGroupListCompositeResult> findCompositeByCriteria(
            ProductGroupSearchCriteria criteria) {

        List<Tuple> rows =
                queryFactory
                        .select(
                                pg.id,
                                pg.sellerId,
                                seller.sellerName,
                                pg.brandId,
                                brand.nameKo,
                                pg.categoryId,
                                category.nameKo,
                                category.displayPath,
                                category.depth,
                                category.department,
                                category.categoryGroup,
                                pg.productGroupName,
                                pg.optionType,
                                pg.status,
                                pg.createdAt,
                                pg.updatedAt)
                        .from(pg)
                        .leftJoin(seller)
                        .on(pg.sellerId.eq(seller.id))
                        .leftJoin(brand)
                        .on(pg.brandId.eq(brand.id))
                        .leftJoin(category)
                        .on(pg.categoryId.eq(category.id))
                        .where(
                                conditionBuilder.idIn(criteria.productGroupIds()),
                                conditionBuilder.sellerIdIn(criteria.sellerIds()),
                                conditionBuilder.brandIdIn(criteria.brandIds()),
                                conditionBuilder.categoryIdIn(criteria.categoryIds()),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.statusNotDeleted())
                        .orderBy(resolveOrderSpecifier(criteria))
                        .offset(criteria.offset())
                        .limit(criteria.size())
                        .fetch();

        if (rows.isEmpty()) {
            return List.of();
        }

        List<Long> pgIds = rows.stream().map(r -> r.get(pg.id)).toList();
        Map<Long, String> thumbnailMap = findThumbnailUrls(pgIds);
        Map<Long, Integer> productCountMap = countProductsByGroupIds(pgIds);

        List<ProductGroupListCompositeResult> results = new ArrayList<>();
        for (Tuple row : rows) {
            Long pgId = row.get(pg.id);
            results.add(
                    ProductGroupListCompositeResult.ofBase(
                            pgId,
                            row.get(pg.sellerId),
                            row.get(seller.sellerName),
                            row.get(pg.brandId),
                            row.get(brand.nameKo),
                            row.get(pg.categoryId),
                            row.get(category.nameKo),
                            row.get(category.displayPath),
                            safeInt(row.get(category.depth)),
                            row.get(category.department),
                            row.get(category.categoryGroup),
                            row.get(pg.productGroupName),
                            row.get(pg.optionType),
                            row.get(pg.status),
                            thumbnailMap.getOrDefault(pgId, null),
                            productCountMap.getOrDefault(pgId, 0),
                            row.get(pg.createdAt),
                            row.get(pg.updatedAt)));
        }
        return results;
    }

    /** 조건별 카운트. */
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(pg.count())
                        .from(pg)
                        .where(
                                conditionBuilder.idIn(criteria.productGroupIds()),
                                conditionBuilder.sellerIdIn(criteria.sellerIds()),
                                conditionBuilder.brandIdIn(criteria.brandIds()),
                                conditionBuilder.categoryIdIn(criteria.categoryIds()),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.statusNotDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /** 가격 + 옵션 Enrichment 배치 조회. */
    public List<ProductGroupEnrichmentResult> findEnrichmentsByProductGroupIds(
            List<Long> productGroupIds) {
        if (productGroupIds == null || productGroupIds.isEmpty()) {
            return List.of();
        }

        List<Tuple> priceTuples =
                queryFactory
                        .select(
                                product.productGroupId,
                                product.currentPrice.min(),
                                product.currentPrice.max(),
                                product.discountRate.max())
                        .from(product)
                        .where(product.productGroupId.in(productGroupIds))
                        .groupBy(product.productGroupId)
                        .fetch();

        Map<Long, int[]> priceMap = new LinkedHashMap<>();
        for (Tuple t : priceTuples) {
            Long pgId = t.get(product.productGroupId);
            priceMap.put(
                    pgId,
                    new int[] {
                        safeInt(t.get(product.currentPrice.min())),
                        safeInt(t.get(product.currentPrice.max())),
                        safeInt(t.get(product.discountRate.max()))
                    });
        }

        Map<Long, List<OptionGroupSummaryResult>> optionMap =
                findOptionSummariesByProductGroupIds(productGroupIds);

        List<ProductGroupEnrichmentResult> results = new ArrayList<>();
        for (Long pgId : productGroupIds) {
            int[] prices = priceMap.getOrDefault(pgId, new int[] {0, 0, 0});
            List<OptionGroupSummaryResult> options = optionMap.getOrDefault(pgId, List.of());
            results.add(
                    new ProductGroupEnrichmentResult(
                            pgId, prices[0], prices[1], prices[2], options));
        }
        return results;
    }

    /** 상세용 Composite 조회 (정책 포함). */
    public Optional<ProductGroupDetailCompositeQueryResult> findDetailCompositeById(
            Long productGroupId) {

        Tuple row =
                queryFactory
                        .select(
                                pg.id,
                                pg.sellerId,
                                seller.sellerName,
                                pg.brandId,
                                brand.nameKo,
                                pg.categoryId,
                                category.nameKo,
                                category.displayPath,
                                pg.productGroupName,
                                pg.optionType,
                                pg.status,
                                pg.createdAt,
                                pg.updatedAt,
                                pg.shippingPolicyId,
                                pg.refundPolicyId)
                        .from(pg)
                        .leftJoin(seller)
                        .on(pg.sellerId.eq(seller.id))
                        .leftJoin(brand)
                        .on(pg.brandId.eq(brand.id))
                        .leftJoin(category)
                        .on(pg.categoryId.eq(category.id))
                        .where(
                                conditionBuilder.idEq(productGroupId),
                                conditionBuilder.statusNotDeleted())
                        .fetchOne();

        if (row == null) {
            return Optional.empty();
        }

        Long shippingPolicyId = row.get(pg.shippingPolicyId);
        Long refundPolicyId = row.get(pg.refundPolicyId);

        ShippingPolicyResult shippingResult = findShippingPolicy(shippingPolicyId);
        RefundPolicyResult refundResult = findRefundPolicy(refundPolicyId);

        return Optional.of(
                new ProductGroupDetailCompositeQueryResult(
                        row.get(pg.id),
                        row.get(pg.sellerId),
                        row.get(seller.sellerName),
                        row.get(pg.brandId),
                        row.get(brand.nameKo),
                        row.get(pg.categoryId),
                        row.get(category.nameKo),
                        row.get(category.displayPath),
                        row.get(pg.productGroupName),
                        row.get(pg.optionType),
                        row.get(pg.status),
                        row.get(pg.createdAt),
                        row.get(pg.updatedAt),
                        shippingResult,
                        refundResult));
    }

    // ── Private 헬퍼 ──

    private String findThumbnailUrl(Long productGroupId) {
        return queryFactory
                .select(pgImage.originUrl)
                .from(pgImage)
                .where(pgImage.productGroupId.eq(productGroupId), pgImage.imageType.eq("THUMBNAIL"))
                .fetchFirst();
    }

    private Map<Long, String> findThumbnailUrls(List<Long> productGroupIds) {
        List<Tuple> thumbnails =
                queryFactory
                        .select(pgImage.productGroupId, pgImage.originUrl)
                        .from(pgImage)
                        .where(
                                pgImage.productGroupId.in(productGroupIds),
                                pgImage.imageType.eq("THUMBNAIL"))
                        .fetch();

        Map<Long, String> map = new LinkedHashMap<>();
        for (Tuple t : thumbnails) {
            map.putIfAbsent(t.get(pgImage.productGroupId), t.get(pgImage.originUrl));
        }
        return map;
    }

    private int countProducts(Long productGroupId) {
        Long count =
                queryFactory
                        .select(product.count())
                        .from(product)
                        .where(product.productGroupId.eq(productGroupId))
                        .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    private Map<Long, Integer> countProductsByGroupIds(List<Long> productGroupIds) {
        List<Tuple> counts =
                queryFactory
                        .select(product.productGroupId, product.count())
                        .from(product)
                        .where(product.productGroupId.in(productGroupIds))
                        .groupBy(product.productGroupId)
                        .fetch();

        Map<Long, Integer> map = new LinkedHashMap<>();
        for (Tuple t : counts) {
            Long cnt = t.get(product.count());
            map.put(t.get(product.productGroupId), cnt != null ? cnt.intValue() : 0);
        }
        return map;
    }

    private Map<Long, List<OptionGroupSummaryResult>> findOptionSummariesByProductGroupIds(
            List<Long> productGroupIds) {
        List<Tuple> groups =
                queryFactory
                        .select(
                                optionGroup.id,
                                optionGroup.productGroupId,
                                optionGroup.optionGroupName)
                        .from(optionGroup)
                        .where(optionGroup.productGroupId.in(productGroupIds))
                        .orderBy(optionGroup.sortOrder.asc())
                        .fetch();

        if (groups.isEmpty()) {
            return Map.of();
        }

        List<Long> groupIds = groups.stream().map(g -> g.get(optionGroup.id)).toList();

        List<Tuple> values =
                queryFactory
                        .select(optionValue.sellerOptionGroupId, optionValue.optionValueName)
                        .from(optionValue)
                        .where(optionValue.sellerOptionGroupId.in(groupIds))
                        .orderBy(optionValue.sortOrder.asc())
                        .fetch();

        Map<Long, List<String>> valuesByGroupId = new LinkedHashMap<>();
        for (Tuple v : values) {
            valuesByGroupId
                    .computeIfAbsent(v.get(optionValue.sellerOptionGroupId), k -> new ArrayList<>())
                    .add(v.get(optionValue.optionValueName));
        }

        Map<Long, List<OptionGroupSummaryResult>> result = new LinkedHashMap<>();
        for (Tuple g : groups) {
            Long pgId = g.get(optionGroup.productGroupId);
            String groupName = g.get(optionGroup.optionGroupName);
            Long grpId = g.get(optionGroup.id);
            List<String> valueNames = valuesByGroupId.getOrDefault(grpId, List.of());

            result.computeIfAbsent(pgId, k -> new ArrayList<>())
                    .add(new OptionGroupSummaryResult(groupName, valueNames));
        }
        return result;
    }

    private ShippingPolicyResult findShippingPolicy(Long policyId) {
        if (policyId == null) {
            return null;
        }

        Tuple row =
                queryFactory
                        .select(
                                shippingPolicy.id,
                                shippingPolicy.sellerId,
                                shippingPolicy.policyName,
                                shippingPolicy.defaultPolicy,
                                shippingPolicy.active,
                                shippingPolicy.shippingFeeType,
                                shippingPolicy.baseFee,
                                shippingPolicy.freeThreshold,
                                shippingPolicy.jejuExtraFee,
                                shippingPolicy.islandExtraFee,
                                shippingPolicy.returnFee,
                                shippingPolicy.exchangeFee,
                                shippingPolicy.leadTimeMinDays,
                                shippingPolicy.leadTimeMaxDays,
                                shippingPolicy.leadTimeCutoffTime,
                                shippingPolicy.createdAt,
                                shippingPolicy.updatedAt)
                        .from(shippingPolicy)
                        .where(shippingPolicy.id.eq(policyId))
                        .fetchOne();

        if (row == null) {
            return null;
        }

        return new ShippingPolicyResult(
                row.get(shippingPolicy.id),
                row.get(shippingPolicy.sellerId),
                row.get(shippingPolicy.policyName),
                Boolean.TRUE.equals(row.get(shippingPolicy.defaultPolicy)),
                Boolean.TRUE.equals(row.get(shippingPolicy.active)),
                row.get(shippingPolicy.shippingFeeType),
                resolveShippingFeeTypeDisplayName(row.get(shippingPolicy.shippingFeeType)),
                toLong(row.get(shippingPolicy.baseFee)),
                toLong(row.get(shippingPolicy.freeThreshold)),
                toLong(row.get(shippingPolicy.jejuExtraFee)),
                toLong(row.get(shippingPolicy.islandExtraFee)),
                toLong(row.get(shippingPolicy.returnFee)),
                toLong(row.get(shippingPolicy.exchangeFee)),
                safeInt(row.get(shippingPolicy.leadTimeMinDays)),
                safeInt(row.get(shippingPolicy.leadTimeMaxDays)),
                row.get(shippingPolicy.leadTimeCutoffTime),
                row.get(shippingPolicy.createdAt),
                row.get(shippingPolicy.updatedAt));
    }

    private RefundPolicyResult findRefundPolicy(Long policyId) {
        if (policyId == null) {
            return null;
        }

        Tuple row =
                queryFactory
                        .select(
                                refundPolicy.id, refundPolicy.sellerId,
                                refundPolicy.policyName, refundPolicy.defaultPolicy,
                                refundPolicy.active, refundPolicy.returnPeriodDays,
                                refundPolicy.exchangePeriodDays,
                                        refundPolicy.nonReturnableConditions,
                                refundPolicy.partialRefundEnabled, refundPolicy.inspectionRequired,
                                refundPolicy.inspectionPeriodDays, refundPolicy.additionalInfo,
                                refundPolicy.createdAt, refundPolicy.updatedAt)
                        .from(refundPolicy)
                        .where(refundPolicy.id.eq(policyId))
                        .fetchOne();

        if (row == null) {
            return null;
        }

        return new RefundPolicyResult(
                row.get(refundPolicy.id),
                row.get(refundPolicy.sellerId),
                row.get(refundPolicy.policyName),
                Boolean.TRUE.equals(row.get(refundPolicy.defaultPolicy)),
                Boolean.TRUE.equals(row.get(refundPolicy.active)),
                safeInt(row.get(refundPolicy.returnPeriodDays)),
                safeInt(row.get(refundPolicy.exchangePeriodDays)),
                List.of(),
                Boolean.TRUE.equals(row.get(refundPolicy.partialRefundEnabled)),
                Boolean.TRUE.equals(row.get(refundPolicy.inspectionRequired)),
                safeInt(row.get(refundPolicy.inspectionPeriodDays)),
                row.get(refundPolicy.additionalInfo),
                row.get(refundPolicy.createdAt),
                row.get(refundPolicy.updatedAt));
    }

    private String resolveShippingFeeTypeDisplayName(String feeType) {
        if (feeType == null) {
            return null;
        }
        return feeType;
    }

    private static int safeInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    private static Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ProductGroupSearchCriteria criteria) {
        ProductGroupSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction.isAscending();

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? pg.createdAt.asc() : pg.createdAt.desc();
            case UPDATED_AT -> isAsc ? pg.updatedAt.asc() : pg.updatedAt.desc();
            case NAME -> isAsc ? pg.productGroupName.asc() : pg.productGroupName.desc();
        };
    }
}
