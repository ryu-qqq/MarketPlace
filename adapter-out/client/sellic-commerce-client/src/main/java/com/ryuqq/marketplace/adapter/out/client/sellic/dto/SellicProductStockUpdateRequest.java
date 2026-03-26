package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 셀릭 재고 수정 요청 DTO.
 *
 * <p>POST /openapi/edit_stock
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellicProductStockUpdateRequest(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("api_key") String apiKey,
        @JsonProperty("product_id") String productId,
        @JsonProperty("option_name1") String optionName1,
        @JsonProperty("option_name2") String optionName2,
        @JsonProperty("option_name3") String optionName3,
        @JsonProperty("option_name4") String optionName4,
        @JsonProperty("product_stocks")
                List<SellicProductRegistrationRequest.SellicProductStock> productStocks) {}
