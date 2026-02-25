package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto;

/**
 * 레거시 상품그룹 이미지 Projection DTO.
 *
 * <p>세토프 ProductImageDto 호환: type + productImageUrl.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public record LegacyProductGroupImageQueryDto(String imageType, String imageUrl) {}
