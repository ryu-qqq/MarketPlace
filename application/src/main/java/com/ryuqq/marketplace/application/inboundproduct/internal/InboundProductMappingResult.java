package com.ryuqq.marketplace.application.inboundproduct.internal;

public record InboundProductMappingResult(
        Long internalBrandId, Long internalCategoryId, boolean isFullyMapped) {

    public static InboundProductMappingResult of(Long brandId, Long categoryId) {
        return new InboundProductMappingResult(
                brandId, categoryId, brandId != null && categoryId != null);
    }
}
