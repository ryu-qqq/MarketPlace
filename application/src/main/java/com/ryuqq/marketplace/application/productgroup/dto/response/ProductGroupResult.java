package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;

/** 상품 그룹 조회 결과 DTO (목록용). */
public record ProductGroupResult(
        Long id,
        Long sellerId,
        Long brandId,
        Long categoryId,
        Long shippingPolicyId,
        Long refundPolicyId,
        String productGroupName,
        String optionType,
        String status,
        List<ProductGroupImageResult> images,
        List<SellerOptionGroupResult> sellerOptionGroups,
        Instant createdAt,
        Instant updatedAt) {

    public static ProductGroupResult from(ProductGroup group) {
        List<ProductGroupImageResult> imageResults =
                group.images().stream().map(ProductGroupImageResult::from).toList();

        List<SellerOptionGroupResult> optionGroupResults =
                group.sellerOptionGroups().stream().map(SellerOptionGroupResult::from).toList();

        return new ProductGroupResult(
                group.idValue(),
                group.sellerIdValue(),
                group.brandIdValue(),
                group.categoryIdValue(),
                group.shippingPolicyIdValue(),
                group.refundPolicyIdValue(),
                group.productGroupNameValue(),
                group.optionType().name(),
                group.status().name(),
                imageResults,
                optionGroupResults,
                group.createdAt(),
                group.updatedAt());
    }
}
