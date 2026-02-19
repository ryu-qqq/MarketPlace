package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 셀러 주소 수정 요청. */
@Schema(description = "셀러 주소 수정 요청")
public record UpdateSellerAddressApiRequest(
        @Schema(description = "주소명", example = "물류센터") String addressName,
        @Schema(description = "주소 정보", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Valid
                AddressRequest address,
        @Schema(description = "기본 주소로 설정 여부 (생략 시 변경 없음)") Boolean defaultAddress) {

    /** 주소 요청. */
    @Schema(description = "주소 정보")
    public record AddressRequest(
            @Schema(description = "우편번호", example = "06164") @NotBlank String zipCode,
            @Schema(description = "도로명주소", example = "서울 강남구 역삼로 123") @NotBlank String line1,
            @Schema(description = "상세주소", example = "5층") String line2) {}
}
