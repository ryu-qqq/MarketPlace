package com.ryuqq.marketplace.application.legacy.productcontext.dto.command;

/**
 * 레거시 환불 데이터.
 *
 * <p>디폴트 환불정책과 비교하기 위한 레거시 환불 정보.
 */
public record LegacyRefundData(
        String returnMethodDomestic,
        String returnCourierDomestic,
        int returnChargeDomestic,
        String returnExchangeAreaDomestic) {}
