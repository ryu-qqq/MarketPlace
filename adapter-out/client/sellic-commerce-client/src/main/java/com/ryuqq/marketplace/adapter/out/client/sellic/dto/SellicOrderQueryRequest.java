package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 셀릭 주문서 조회 요청 DTO.
 *
 * <p>POST /openapi/get_order
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellicOrderQueryRequest(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("api_key") String apiKey,
        @JsonProperty("s_date") String startDate,
        @JsonProperty("e_date") String endDate,
        @JsonProperty("order_id") Integer orderId,
        @JsonProperty("mall_order_id") String mallOrderId) {}
