package com.ryuqq.marketplace.application.selleraddress.dto.composite;

/**
 * 셀러 운영 메타데이터 Composite 조회 결과.
 *
 * <p>주소, 배송정책, 환불정책의 메타데이터를 한방 쿼리로 조회한 결과.
 *
 * @param addressTotalCount 전체 주소 수
 * @param shippingAddressCount 출고지 수
 * @param returnAddressCount 반품지 수
 * @param hasDefaultShippingAddress 기본 출고지 설정 여부
 * @param hasDefaultReturnAddress 기본 반품지 설정 여부
 * @param shippingPolicyCount 배송정책 수
 * @param hasDefaultShippingPolicy 기본 배송정책 설정 여부
 * @param refundPolicyCount 환불정책 수
 * @param hasDefaultRefundPolicy 기본 환불정책 설정 여부
 */
public record SellerOperationCompositeResult(
        long addressTotalCount,
        long shippingAddressCount,
        long returnAddressCount,
        boolean hasDefaultShippingAddress,
        boolean hasDefaultReturnAddress,
        long shippingPolicyCount,
        boolean hasDefaultShippingPolicy,
        long refundPolicyCount,
        boolean hasDefaultRefundPolicy) {}
