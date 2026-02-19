package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 CreateRefundNotice 호환 요청 DTO. */
public record LegacyCreateRefundNoticeRequest(
        String returnMethodDomestic,
        String returnCourierDomestic,
        int returnChargeDomestic,
        String returnExchangeAreaDomestic) {}
