package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 UpdateProductStock 호환 요청 DTO. */
public record LegacyUpdateProductStockRequest(long productId, int productStockQuantity) {}
