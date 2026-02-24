package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCompositionReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup Read Facade.
 *
 * <p>ReadManager들만 조합하여 조회 결과를 번들 DTO로 묶어 반환합니다. 조립(Assembling)은 Service에서 Assembler를 통해 수행합니다.
 */
@Component
public class ProductGroupReadFacade {

    private final ProductGroupCompositionReadManager compositionReadManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final ProductReadManager productReadManager;
    private final ProductNoticeReadManager productNoticeReadManager;
    private final ProductGroupImageReadManager imageReadManager;

    public ProductGroupReadFacade(
            ProductGroupCompositionReadManager compositionReadManager,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupDescriptionReadManager descriptionReadManager,
            ProductReadManager productReadManager,
            ProductNoticeReadManager productNoticeReadManager,
            ProductGroupImageReadManager imageReadManager) {
        this.compositionReadManager = compositionReadManager;
        this.productGroupReadManager = productGroupReadManager;
        this.descriptionReadManager = descriptionReadManager;
        this.productReadManager = productReadManager;
        this.productNoticeReadManager = productNoticeReadManager;
        this.imageReadManager = imageReadManager;
    }

    /**
     * Offset 기반 목록 조회 번들.
     *
     * <p>1단계: 기본 Composition 쿼리 (필터 + 페이징) + 전체 건수
     *
     * <p>2단계: productGroupIds 추출 → 가격/옵션 통합 enrichment (IN 쿼리 1회)
     *
     * <p>3단계: 번들로 묶어 반환 (조립은 Service에서 Assembler가 수행)
     */
    @Transactional(readOnly = true)
    public ProductGroupListBundle getListBundle(ProductGroupSearchCriteria criteria) {
        List<ProductGroupListCompositeResult> baseComposites =
                compositionReadManager.findCompositeByCriteria(criteria);
        long totalElements = compositionReadManager.countByCriteria(criteria);

        if (baseComposites.isEmpty()) {
            return new ProductGroupListBundle(List.of(), List.of(), totalElements);
        }

        List<Long> productGroupIds =
                baseComposites.stream().map(ProductGroupListCompositeResult::id).toList();

        List<ProductGroupEnrichmentResult> enrichments =
                compositionReadManager.findEnrichments(productGroupIds);

        return new ProductGroupListBundle(baseComposites, enrichments, totalElements);
    }

    /**
     * 엑셀 다운로드용 번들.
     *
     * <p>1단계: 기본 Composition 쿼리 (필터 + 페이징) + 전체 건수
     *
     * <p>2단계: productGroupIds 추출 → IN절 배치 조회 (enrichment, images, products, descriptions, notices)
     *
     * <p>3단계: 도메인 객체를 application DTO로 변환 + productGroupId별 Map으로 그룹핑
     *
     * <p>4단계: 번들로 묶어 반환 (조립은 Service에서 Assembler가 수행)
     */
    @Transactional(readOnly = true)
    public ProductGroupExcelBundle getExcelBundle(ProductGroupSearchCriteria criteria) {
        List<ProductGroupListCompositeResult> baseComposites =
                compositionReadManager.findCompositeByCriteria(criteria);
        long totalElements = compositionReadManager.countByCriteria(criteria);

        if (baseComposites.isEmpty()) {
            return new ProductGroupExcelBundle(
                    List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 0);
        }

        List<Long> ids = baseComposites.stream().map(ProductGroupListCompositeResult::id).toList();
        List<ProductGroupId> productGroupIds = ids.stream().map(ProductGroupId::of).toList();

        List<ProductGroupEnrichmentResult> enrichments =
                compositionReadManager.findEnrichments(ids);

        Map<Long, List<ProductGroupImageResult>> imageMap =
                imageReadManager.findByProductGroupIds(productGroupIds).stream()
                        .collect(
                                Collectors.groupingBy(
                                        ProductGroupImage::productGroupIdValue,
                                        Collectors.mapping(
                                                ProductGroupImageResult::from,
                                                Collectors.toList())));

        Map<Long, List<ProductResult>> productMap =
                productReadManager.findByProductGroupIds(productGroupIds).stream()
                        .collect(
                                Collectors.groupingBy(
                                        Product::productGroupIdValue,
                                        Collectors.mapping(
                                                ProductResult::from, Collectors.toList())));

        Map<Long, String> descriptionCdnUrlMap =
                descriptionReadManager.findByProductGroupIds(productGroupIds).stream()
                        .collect(
                                Collectors.toMap(
                                        ProductGroupDescription::productGroupIdValue,
                                        ProductGroupDescription::cdnPathValue,
                                        (a, b) -> a));

        Map<Long, ProductNoticeResult> noticeMap =
                productNoticeReadManager.findByProductGroupIds(productGroupIds).stream()
                        .collect(
                                Collectors.toMap(
                                        ProductNotice::productGroupIdValue,
                                        ProductNoticeResult::from,
                                        (a, b) -> a));

        return new ProductGroupExcelBundle(
                baseComposites,
                enrichments,
                imageMap,
                productMap,
                descriptionCdnUrlMap,
                noticeMap,
                totalElements);
    }

    /**
     * 상세 조회 번들.
     *
     * <p>1단계: Composition 쿼리로 기본 정보 + 배송/환불 정책 조회 (JOIN 1회)
     *
     * <p>2단계: ProductGroup Aggregate 조회 (이미지, 옵션 구조)
     *
     * <p>3단계: Product, Description, Notice 개별 조회
     *
     * <p>4단계: 번들로 묶어 반환 (조립은 Service에서 Assembler가 수행)
     */
    @Transactional(readOnly = true)
    public ProductGroupDetailBundle getDetailBundle(Long productGroupId) {
        ProductGroupId groupId = ProductGroupId.of(productGroupId);

        ProductGroupDetailCompositeQueryResult queryResult =
                compositionReadManager.getDetailCompositeById(productGroupId);

        ProductGroup group = productGroupReadManager.getById(groupId);

        List<Product> products = productReadManager.findByProductGroupId(groupId);

        Optional<ProductGroupDescription> description =
                descriptionReadManager.findByProductGroupId(groupId);

        Optional<ProductNotice> notice = productNoticeReadManager.findByProductGroupId(groupId);

        return new ProductGroupDetailBundle(queryResult, group, products, description, notice);
    }
}
