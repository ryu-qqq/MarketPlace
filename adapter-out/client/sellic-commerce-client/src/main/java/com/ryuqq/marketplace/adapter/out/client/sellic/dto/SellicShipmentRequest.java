package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 셀릭 송장 등록 요청 DTO.
 *
 * <p>POST /openapi/set_ship
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellicShipmentRequest(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("api_key") String apiKey,
        @JsonProperty("ships") List<SellicShipEntry> ships) {

    /**
     * 개별 송장 항목.
     */
    public record SellicShipEntry(
            @JsonProperty("order_id") Integer orderId,
            @JsonProperty("delivery") Integer delivery,
            @JsonProperty("invoice") String invoice,
            @JsonProperty("delivery_hope_at") String deliveryHopeAt) {}
}
