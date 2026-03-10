package com.ryuqq.marketplace.adapter.out.client.setof.dto;

/**
 * 세토프 커머스 상품 가격 수정 요청 DTO.
 *
 * <p>PATCH /api/v2/admin/products/{productId}/price 요청 본문.
 */
public record SetofProductPriceUpdateRequest(Integer regularPrice, Integer currentPrice) {}
