package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

/** 인바운드 상품 수신 요청 DTO (크롤링 등 외부 소스용). */
public record ReceiveInboundProductApiRequest(
        long inboundSourceId,
        String externalProductCode,
        String productName,
        String externalBrandCode,
        String externalCategoryCode,
        long sellerId,
        int regularPrice,
        int currentPrice,
        String optionType,
        String descriptionHtml,
        String rawPayloadJson) {}
