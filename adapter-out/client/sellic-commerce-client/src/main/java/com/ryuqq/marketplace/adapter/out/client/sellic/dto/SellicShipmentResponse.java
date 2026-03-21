package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 셀릭 송장 등록 응답 DTO.
 *
 * <p>POST /openapi/set_ship 응답.
 */
public record SellicShipmentResponse(
        @JsonProperty("result") String result,
        @JsonProperty("message") String message,
        @JsonProperty("datas") List<SellicShipResultData> datas) {

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(result);
    }

    public record SellicShipResultData(
            @JsonProperty("result") String result,
            @JsonProperty("order_id") Integer orderId,
            @JsonProperty("msg") String msg) {

        public boolean isSuccess() {
            return "success".equalsIgnoreCase(result);
        }
    }
}
