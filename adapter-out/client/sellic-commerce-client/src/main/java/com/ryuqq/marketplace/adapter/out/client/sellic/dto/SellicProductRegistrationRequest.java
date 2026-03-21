package com.ryuqq.marketplace.adapter.out.client.sellic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 셀릭 상품 등록 요청 DTO.
 *
 * <p>POST /openapi/set_product
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellicProductRegistrationRequest(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("api_key") String apiKey,
        @JsonProperty("product_name") String productName,
        @JsonProperty("own_code") String ownCode,
        @JsonProperty("origin") Integer origin,
        @JsonProperty("supplier_name") String supplierName,
        @JsonProperty("category_id") Integer categoryId,
        @JsonProperty("sale_status") Integer saleStatus,
        @JsonProperty("delivery_charge_type") Integer deliveryChargeType,
        @JsonProperty("delivery_fee") String deliveryFee,
        @JsonProperty("tax") Integer tax,
        @JsonProperty("brand") String brand,
        @JsonProperty("model") String model,
        @JsonProperty("model_no") String modelNo,
        @JsonProperty("keyword") String keyword,
        @JsonProperty("detail_note") String detailNote,
        @JsonProperty("market_price") Integer marketPrice,
        @JsonProperty("sale_price") Integer salePrice,
        @JsonProperty("image1") String image1,
        @JsonProperty("image7") String image7,
        @JsonProperty("image8") String image8,
        @JsonProperty("image9") String image9,
        @JsonProperty("image10") String image10,
        @JsonProperty("image11") String image11,
        @JsonProperty("image12") String image12,
        @JsonProperty("image13") String image13,
        @JsonProperty("image14") String image14,
        @JsonProperty("image15") String image15,
        @JsonProperty("image16") String image16,
        @JsonProperty("image17") String image17,
        @JsonProperty("image18") String image18,
        @JsonProperty("image19") String image19,
        @JsonProperty("image20") String image20,
        @JsonProperty("image21") String image21,
        @JsonProperty("image22") String image22,
        @JsonProperty("nofity_code") Integer notifyCode,
        @JsonProperty("nofity1") String notify1,
        @JsonProperty("nofity2") String notify2,
        @JsonProperty("nofity3") String notify3,
        @JsonProperty("nofity4") String notify4,
        @JsonProperty("nofity5") String notify5,
        @JsonProperty("nofity6") String notify6,
        @JsonProperty("nofity7") String notify7,
        @JsonProperty("nofity8") String notify8,
        @JsonProperty("nofity9") String notify9,
        @JsonProperty("nofity10") String notify10,
        @JsonProperty("nofity11") String notify11,
        @JsonProperty("nofity12") String notify12,
        @JsonProperty("nofity13") String notify13,
        @JsonProperty("nofity14") String notify14,
        @JsonProperty("nofity15") String notify15,
        @JsonProperty("option_name1") String optionName1,
        @JsonProperty("option_name2") String optionName2,
        @JsonProperty("option_name3") String optionName3,
        @JsonProperty("option_name4") String optionName4,
        @JsonProperty("product_stocks") List<SellicProductStock> productStocks) {

    /**
     * 셀릭 상품 옵션/재고 항목.
     */
    public record SellicProductStock(
            @JsonProperty("present_stock") Integer presentStock,
            @JsonProperty("option_item1") String optionItem1,
            @JsonProperty("option_item2") String optionItem2,
            @JsonProperty("option_item3") String optionItem3,
            @JsonProperty("option_item4") String optionItem4,
            @JsonProperty("stock_barcode") String stockBarcode,
            @JsonProperty("stock_name") String stockName,
            @JsonProperty("add_price") Integer addPrice) {}
}
