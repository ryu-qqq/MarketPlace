package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 네이버 커머스 상품 목록 조회 응답 DTO.
 *
 * <p>POST /v1/products/search 응답.
 *
 * @see <a href="https://apicenter.commerce.naver.com/docs/commerce-api/current/search-product">상품
 *     목록 조회</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverProductSearchResponse(
        List<ProductContent> contents,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages,
        Boolean first,
        Boolean last) {

    /** 상품 콘텐츠 (그룹상품 단위). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductContent(
            Long groupProductNo, Long originProductNo, List<ChannelProduct> channelProducts) {}

    /** 채널 상품 정보. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChannelProduct(
            Long groupProductNo,
            Long originProductNo,
            Long channelProductNo,
            String channelServiceType,
            String categoryId,
            String name,
            String sellerManagementCode,
            String statusType,
            String channelProductDisplayStatusType,
            Long salePrice,
            Long discountedPrice,
            Integer stockQuantity,
            String deliveryAttributeType,
            Long deliveryFee,
            String wholeCategoryName,
            String wholeCategoryId,
            RepresentativeImage representativeImage,
            String brandName,
            String manufacturerName,
            String regDate,
            String modifiedDate) {}

    /** 대표 이미지. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RepresentativeImage(String url) {}
}
