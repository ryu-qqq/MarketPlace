package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 주소 등록 응답. */
@Schema(description = "셀러 주소 등록 응답")
public record RegisterSellerAddressApiResponse(
        @Schema(description = "생성된 주소 ID", example = "1") Long addressId) {}
