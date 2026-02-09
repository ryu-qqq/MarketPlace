package com.ryuqq.marketplace.application.selleraddress.dto.response;

/**
 * 셀러 운영 메타데이터 조회 결과.
 *
 * @param totalCount 전체 주소 수
 * @param shippingCount 출고지 수
 * @param returnCount 반품지 수
 * @param hasDefaultShipping 기본 출고지 설정 여부
 * @param hasDefaultReturn 기본 반품지 설정 여부
 * @param shippingPolicyCount 배송정책 수
 * @param refundPolicyCount 환불정책 수
 * @param hasDefaultShippingPolicy 기본 배송정책 설정 여부
 * @param hasDefaultRefundPolicy 기본 환불정책 설정 여부
 */
public record SellerOperationMetadataResult(
        long totalCount,
        long shippingCount,
        long returnCount,
        boolean hasDefaultShipping,
        boolean hasDefaultReturn,
        long shippingPolicyCount,
        long refundPolicyCount,
        boolean hasDefaultShippingPolicy,
        boolean hasDefaultRefundPolicy) {}
