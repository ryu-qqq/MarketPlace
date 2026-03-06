package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** OMS 쇼핑몰 응답 (API 7). */
@Schema(description = "쇼핑몰 응답")
public record OmsShopApiResponse(
        @Schema(description = "쇼핑몰 ID", example = "1") long id,
        @Schema(description = "쇼핑몰명", example = "스마트스토어") String shopName,
        @Schema(description = "판매채널 ID", example = "1") long salesChannelId,
        @Schema(description = "계정 ID", example = "trexi001") String accountId,
        @Schema(description = "상태", example = "ACTIVE") String status) {}
