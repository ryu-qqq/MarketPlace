package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 셀릭 주문서 조회 응답 DTO.
 *
 * <p>POST /openapi/get_order 응답.
 */
public record SellicOrderQueryResponse(
        @JsonProperty("result") String result,
        @JsonProperty("message") String message,
        @JsonProperty("datas") List<SellicOrderData> datas) {

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(result);
    }

    /**
     * 셀릭 주문 개별 데이터.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SellicOrderData(
            @JsonProperty("IDX") Integer idx,
            @JsonProperty("ORDER_ID") String orderId,
            @JsonProperty("ORDER_SUB_ID") String orderSubId,
            @JsonProperty("ORIGINAL_ORDER_ID") String originalOrderId,
            @JsonProperty("ORDER_STATUS") Integer orderStatus,
            @JsonProperty("ORDER_DATE") String orderDate,
            @JsonProperty("CREATED_AT") String createdAt,
            @JsonProperty("ORDER_TYPE") Integer orderType,
            @JsonProperty("USER_NAME") String userName,
            @JsonProperty("USER_TEL") String userTel,
            @JsonProperty("USER_CEL") String userCel,
            @JsonProperty("RECEIVE_NAME") String receiveName,
            @JsonProperty("RECEIVE_TEL") String receiveTel,
            @JsonProperty("RECEIVE_CEL") String receiveCel,
            @JsonProperty("RECEIVE_ZIPCODE") String receiveZipcode,
            @JsonProperty("RECEIVE_ADDR") String receiveAddr,
            @JsonProperty("DELV_MSG") String deliveryMessage,
            @JsonProperty("SALE_COST") Integer saleCost,
            @JsonProperty("MALL_WON_COST") Integer mallWonCost,
            @JsonProperty("SALE_CNT") Integer saleCnt,
            @JsonProperty("TOTAL_PRICE") Integer totalPrice,
            @JsonProperty("SETTLEMENT_PRICE") Integer settlementPrice,
            @JsonProperty("PAYMENT_PRICE") Integer paymentPrice,
            @JsonProperty("DELIVERY_FEE") Integer deliveryFee,
            @JsonProperty("PRODUCT_ID") Integer productId,
            @JsonProperty("OPTION_CODE") Integer optionCode,
            @JsonProperty("MALL_PRODUCT_ID") String mallProductId,
            @JsonProperty("PRODUCT_NAME") String productName,
            @JsonProperty("OPTION_NAME") String optionName,
            @JsonProperty("OWN_CODE") String ownCode,
            @JsonProperty("SELLIC_PRODUCT_NAME") String sellicProductName,
            @JsonProperty("OPTION_ITEM") String optionItem,
            @JsonProperty("STOCK_BARCODE") String stockBarcode,
            @JsonProperty("DELIVERY") Integer delivery,
            @JsonProperty("INVOICE") String invoice,
            @JsonProperty("MALL_ID") String mallId,
            @JsonProperty("MALL_NAME") String mallName,
            @JsonProperty("ORDER_CHANNEL") String orderChannel) {}
}
