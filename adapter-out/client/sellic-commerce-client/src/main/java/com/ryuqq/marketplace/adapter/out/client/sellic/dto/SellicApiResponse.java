package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 셀릭 API 공통 응답 DTO.
 *
 * <p>등록 시 product_id 포함, 수정/재고 수정 시 null.
 */
public record SellicApiResponse(
        @JsonProperty("result") String result,
        @JsonProperty("message") String message,
        @JsonProperty("product_id") String productId) {

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(result);
    }
}
