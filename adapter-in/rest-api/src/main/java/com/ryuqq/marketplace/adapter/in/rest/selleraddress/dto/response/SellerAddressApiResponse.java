package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 주소 조회 응답. */
@Schema(description = "셀러 주소 조회 응답")
public record SellerAddressApiResponse(
        @Schema(description = "주소 ID", example = "1") Long id,
        @Schema(description = "셀러 ID", example = "1") Long sellerId,
        @Schema(description = "주소 유형 (SHIPPING, RETURN)", example = "SHIPPING") String addressType,
        @Schema(description = "주소명", example = "본사 창고") String addressName,
        @Schema(description = "주소 정보") AddressResponse address,
        @Schema(description = "기본 주소 여부", example = "true") boolean defaultAddress,
        @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00") String updatedAt) {

    /** 주소 응답. */
    @Schema(description = "주소 정보")
    public record AddressResponse(
            @Schema(description = "우편번호", example = "06164") String zipCode,
            @Schema(description = "도로명주소", example = "서울 강남구 역삼로 123") String line1,
            @Schema(description = "상세주소", example = "5층") String line2) {}
}
