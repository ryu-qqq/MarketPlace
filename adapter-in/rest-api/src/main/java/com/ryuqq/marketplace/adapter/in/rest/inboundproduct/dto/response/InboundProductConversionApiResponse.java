package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response;

/** 인바운드 상품 변환 결과 응답 DTO. */
public record InboundProductConversionApiResponse(
        Long inboundProductId, Long internalProductGroupId, String status, String action) {}
