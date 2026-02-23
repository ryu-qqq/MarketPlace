package com.ryuqq.marketplace.application.productgroup.assembler;

import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductOptionMatrixResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.ProductGroupDescriptionResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** ProductGroup Assembler. */
@Component
public class ProductGroupAssembler {

    /**
     * 목록 번들 → PageResult 조립.
     *
     * <p>기본 Composite에 enrichment를 적용한 뒤 PageResult로 변환합니다.
     */
    public ProductGroupPageResult toPageResult(ProductGroupListBundle bundle, int page, int size) {
        if (bundle.baseComposites().isEmpty()) {
            return ProductGroupPageResult.empty(size);
        }

        List<ProductGroupListCompositeResult> enrichedComposites =
                enrichComposites(bundle.baseComposites(), bundle.enrichments());

        return ProductGroupPageResult.of(enrichedComposites, page, size, bundle.totalElements());
    }

    /** 상세 번들 → DetailCompositeResult 조립. */
    public ProductGroupDetailCompositeResult toDetailResult(ProductGroupDetailBundle bundle) {
        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();

        List<ProductGroupImageResult> images =
                group.images().stream().map(ProductGroupImageResult::from).toList();

        ProductOptionMatrixResult optionProductMatrix =
                buildOptionProductMatrix(group, bundle.products());

        ProductGroupDescriptionResult descriptionResult =
                bundle.description().map(ProductGroupDescriptionResult::from).orElse(null);
        ProductNoticeResult noticeResult =
                bundle.notice().map(ProductNoticeResult::from).orElse(null);

        return new ProductGroupDetailCompositeResult(
                queryResult.id(),
                queryResult.sellerId(),
                queryResult.sellerName(),
                queryResult.brandId(),
                queryResult.brandName(),
                queryResult.categoryId(),
                queryResult.categoryName(),
                queryResult.categoryDisplayPath(),
                queryResult.categoryIdPath(),
                queryResult.productGroupName(),
                queryResult.optionType(),
                queryResult.status(),
                queryResult.createdAt(),
                queryResult.updatedAt(),
                images,
                optionProductMatrix,
                queryResult.shippingPolicy(),
                queryResult.refundPolicy(),
                descriptionResult,
                noticeResult);
    }

    private List<ProductGroupListCompositeResult> enrichComposites(
            List<ProductGroupListCompositeResult> baseComposites,
            List<ProductGroupEnrichmentResult> enrichments) {

        Map<Long, ProductGroupEnrichmentResult> enrichmentMap =
                enrichments.stream()
                        .collect(
                                Collectors.toMap(
                                        ProductGroupEnrichmentResult::productGroupId,
                                        Function.identity()));

        return baseComposites.stream()
                .map(
                        base -> {
                            ProductGroupEnrichmentResult enrichment = enrichmentMap.get(base.id());
                            if (enrichment == null) {
                                return base;
                            }
                            return base.withEnrichment(
                                    enrichment.minPrice(),
                                    enrichment.maxPrice(),
                                    enrichment.maxDiscountRate(),
                                    enrichment.optionGroups());
                        })
                .toList();
    }

    private ProductOptionMatrixResult buildOptionProductMatrix(
            ProductGroup group, List<Product> products) {
        List<SellerOptionGroupResult> optionGroups =
                group.sellerOptionGroups().stream().map(SellerOptionGroupResult::from).toList();

        Map<Long, ResolvedProductOptionResult> optionValueMap = buildOptionValueMap(group);
        List<ProductDetailResult> productDetails = toProductDetailResults(products, optionValueMap);

        return new ProductOptionMatrixResult(optionGroups, productDetails);
    }

    private Map<Long, ResolvedProductOptionResult> buildOptionValueMap(ProductGroup group) {
        Map<Long, ResolvedProductOptionResult> map = new HashMap<>();
        for (SellerOptionGroup optionGroup : group.sellerOptionGroups()) {
            for (SellerOptionValue optionValue : optionGroup.optionValues()) {
                map.put(
                        optionValue.idValue(),
                        new ResolvedProductOptionResult(
                                optionGroup.idValue(),
                                optionGroup.optionGroupNameValue(),
                                optionValue.idValue(),
                                optionValue.optionValueNameValue()));
            }
        }
        return map;
    }

    private List<ProductDetailResult> toProductDetailResults(
            List<Product> products, Map<Long, ResolvedProductOptionResult> optionValueMap) {
        return products.stream()
                .map(product -> toProductDetailResult(product, optionValueMap))
                .toList();
    }

    private ProductDetailResult toProductDetailResult(
            Product product, Map<Long, ResolvedProductOptionResult> optionValueMap) {
        List<ResolvedProductOptionResult> options =
                product.optionMappings().stream()
                        .map(mapping -> optionValueMap.get(mapping.sellerOptionValueIdValue()))
                        .filter(Objects::nonNull)
                        .toList();

        return new ProductDetailResult(
                product.idValue(),
                product.skuCodeValue(),
                product.regularPriceValue(),
                product.currentPriceValue(),
                product.salePriceValue(),
                product.discountRate(),
                product.stockQuantity(),
                product.status().name(),
                product.sortOrder(),
                options,
                product.createdAt(),
                product.updatedAt());
    }
}
