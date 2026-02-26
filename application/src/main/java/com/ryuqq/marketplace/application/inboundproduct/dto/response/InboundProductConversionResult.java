package com.ryuqq.marketplace.application.inboundproduct.dto.response;

import com.ryuqq.marketplace.domain.inboundproduct.vo.ConversionAction;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;

public record InboundProductConversionResult(
        Long inboundProductId,
        Long internalProductGroupId,
        InboundProductStatus status,
        ConversionAction action) {

    public static InboundProductConversionResult created(
            Long inboundProductId, Long productGroupId) {
        return new InboundProductConversionResult(
                inboundProductId,
                productGroupId,
                InboundProductStatus.CONVERTED,
                ConversionAction.CREATED);
    }

    public static InboundProductConversionResult updated(
            Long inboundProductId, Long productGroupId) {
        return new InboundProductConversionResult(
                inboundProductId,
                productGroupId,
                InboundProductStatus.CONVERTED,
                ConversionAction.UPDATED);
    }

    public static InboundProductConversionResult pendingMapping(Long inboundProductId) {
        return new InboundProductConversionResult(
                inboundProductId,
                null,
                InboundProductStatus.PENDING_MAPPING,
                ConversionAction.PENDING_MAPPING);
    }
}
