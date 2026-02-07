package com.ryuqq.marketplace.application.selleraddress.dto.response;

/**
 * 셀러 주소 메타데이터 조회 결과.
 *
 * @param totalCount 전체 주소 수
 * @param shippingCount 출고지 수
 * @param returnCount 반품지 수
 * @param hasDefaultShipping 기본 출고지 설정 여부
 * @param hasDefaultReturn 기본 반품지 설정 여부
 */
public record SellerAddressMetadataResult(
        long totalCount,
        long shippingCount,
        long returnCount,
        boolean hasDefaultShipping,
        boolean hasDefaultReturn) {}
