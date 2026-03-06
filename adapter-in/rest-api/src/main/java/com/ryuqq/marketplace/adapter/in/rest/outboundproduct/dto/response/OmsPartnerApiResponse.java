package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** OMS 파트너(셀러) 응답 (API 6). */
@Schema(description = "파트너(셀러) 응답")
public record OmsPartnerApiResponse(
        @Schema(description = "셀러 ID", example = "1001") long id,
        @Schema(description = "파트너명", example = "나이키") String partnerName,
        @Schema(description = "파트너 코드", example = "나이키코리아") String partnerCode,
        @Schema(description = "상태", example = "ACTIVE") String status) {}
