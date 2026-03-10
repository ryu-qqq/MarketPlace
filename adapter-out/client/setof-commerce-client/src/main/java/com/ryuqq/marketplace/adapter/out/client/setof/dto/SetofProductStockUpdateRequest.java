package com.ryuqq.marketplace.adapter.out.client.setof.dto;

/**
 * 세토프 커머스 상품 재고 수정 요청 DTO.
 *
 * <p>PATCH /api/v2/admin/products/{productId}/stock 요청 본문.
 */
public record SetofProductStockUpdateRequest(Integer stockQuantity) {}
