package com.ryuqq.marketplace.adapter.out.client.setof.dto;

/**
 * 세토프 커머스 상품 그룹 기본 정보 수정 요청 DTO.
 *
 * <p>PATCH /api/v2/admin/product-groups/{id}/basic-info 요청 본문.
 */
public record SetofProductGroupBasicInfoUpdateRequest(
        String productGroupName,
        Long brandId,
        Long categoryId,
        Long shippingPolicyId,
        Long refundPolicyId) {}
