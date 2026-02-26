package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.util.List;

/**
 * SellerOperationCompositeDto - 셀러 운영 메타데이터 Composite DTO.
 *
 * <p>셀러의 주소, 배송정책, 환불정책 raw 데이터를 담는 Composite DTO.
 *
 * <p>집계(count, hasDefault)는 Adapter Mapper에서 처리.
 */
public record SellerOperationCompositeDto(
        Long sellerId,
        List<AddressSummaryDto> addresses,
        List<PolicySummaryDto> shippingPolicies,
        List<PolicySummaryDto> refundPolicies) {

    /** 주소 요약 DTO - 메타데이터 집계에 필요한 최소 필드만. */
    public record AddressSummaryDto(String addressType, boolean defaultAddress) {}

    /** 정책 요약 DTO - 메타데이터 집계에 필요한 최소 필드만. */
    public record PolicySummaryDto(boolean defaultPolicy) {}
}
