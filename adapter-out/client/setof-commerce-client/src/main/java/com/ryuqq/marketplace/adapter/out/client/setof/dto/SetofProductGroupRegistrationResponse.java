package com.ryuqq.marketplace.adapter.out.client.setof.dto;

/**
 * 세토프 커머스 상품 그룹 등록 응답 DTO.
 *
 * <p>POST /api/v2/admin/product-groups 응답에서 필요한 필드만 추출합니다.
 *
 * @param productGroupId 생성된 상품 그룹 ID
 */
public record SetofProductGroupRegistrationResponse(Long productGroupId) {}
