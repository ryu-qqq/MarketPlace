package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 주소 조회 응답. */
@Schema(description = "셀러 주소 조회 응답")
public record SellerAddressApiResponse(
        @Schema(description = "주소 ID") Long id,
        @Schema(description = "셀러 ID") Long sellerId,
        @Schema(description = "주소 유형") String addressType,
        @Schema(description = "주소명") String addressName,
        @Schema(description = "주소 정보") AddressResponse address,
        @Schema(description = "기본 주소 여부") boolean defaultAddress,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {

    /** 주소 응답. */
    @Schema(description = "주소 정보")
    public record AddressResponse(
            @Schema(description = "우편번호") String zipCode,
            @Schema(description = "도로명주소") String line1,
            @Schema(description = "상세주소") String line2) {}
}
