package com.ryuqq.marketplace.application.inboundproduct.dto.command;

public record ReceiveInboundProductCommand(
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
