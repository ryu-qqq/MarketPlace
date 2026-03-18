package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/** 교환 요청 일괄 API 요청. V4 간극: orderId = 내부 orderItemId. */
@Schema(description = "교환 요청 일괄 요청")
public record RequestExchangeBatchApiRequest(
        @Schema(description = "교환 요청 대상 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                @Valid
                List<ExchangeRequestItemApiRequest> items) {

    @Schema(description = "교환 요청 개별 항목")
    public record ExchangeRequestItemApiRequest(
            @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)", example = "01940001-0000-7000-8000-000000000001", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String orderId,
            @Schema(description = "교환 수량", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                    @Positive
                    int exchangeQty,
            @Schema(description = "교환 사유 유형", example = "CHANGE_OF_MIND", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String reasonType,
            @Schema(description = "교환 상세 사유", example = "사이즈 변경 원합니다")
                    String reasonDetail,
            @Schema(description = "원 상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                    long originalProductId,
            @Schema(description = "원 SKU 코드", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String originalSkuCode,
            @Schema(description = "교환 대상 상품 그룹 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                    long targetProductGroupId,
            @Schema(description = "교환 대상 상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                    long targetProductId,
            @Schema(description = "교환 대상 SKU 코드", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String targetSkuCode,
            @Schema(description = "교환 대상 수량", requiredMode = Schema.RequiredMode.REQUIRED)
                    @Positive
                    int targetQuantity) {}
}
