package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 세토프 커머스 상품 그룹 등록 응답 DTO.
 *
 * <p>POST /api/v2/admin/product-groups 응답 구조: { "data": { "productGroupId": ... }, "timestamp":
 * ..., "requestId": ... }
 *
 * @param data 응답 데이터
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SetofProductGroupRegistrationResponse(Data data) {

    /** 응답 데이터 내부 구조. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(Long productGroupId) {}

    /** data.productGroupId 편의 접근자. */
    public Long productGroupId() {
        return data != null ? data.productGroupId() : null;
    }
}
