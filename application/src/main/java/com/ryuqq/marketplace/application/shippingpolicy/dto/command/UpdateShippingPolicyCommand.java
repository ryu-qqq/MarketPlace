package com.ryuqq.marketplace.application.shippingpolicy.dto.command;

/**
 * 배송정책 수정 Command.
 *
 * @param sellerId 셀러 ID
 * @param policyId 정책 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param shippingFeeType 배송비 유형
 * @param baseFee 기본 배송비
 * @param freeThreshold 무료배송 기준금액
 * @param jejuExtraFee 제주 추가 배송비
 * @param islandExtraFee 도서산간 추가 배송비
 * @param returnFee 반품 배송비
 * @param exchangeFee 교환 배송비
 * @param leadTime 발송 소요일 정보
 */
public record UpdateShippingPolicyCommand(
        Long sellerId,
        Long policyId,
        String policyName,
        Boolean defaultPolicy,
        String shippingFeeType,
        Long baseFee,
        Long freeThreshold,
        Long jejuExtraFee,
        Long islandExtraFee,
        Long returnFee,
        Long exchangeFee,
        LeadTimeCommand leadTime) {}
