package com.ryuqq.marketplace.adapter.in.rest.refund.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/** 환불 요청 일괄 API 요청. V4 간극: orderId = 내부 orderItemId. */
@Schema(description = "환불 요청 일괄 요청")
public record RequestRefundBatchApiRequest(
        @Schema(description = "환불 요청 대상 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                @Valid
                List<RefundRequestItemApiRequest> items) {

    @Schema(description = "환불 요청 개별 항목")
    public record RefundRequestItemApiRequest(
            @Schema(
                            description = "주문 ID (프론트: orderId = 내부 orderItemId)",
                            example = "01940001-0000-7000-8000-000000000001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String orderId,
            @Schema(
                            description = "환불 수량",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @Positive
                    int refundQty,
            @Schema(
                            description = "환불 사유 유형",
                            example = "CHANGE_OF_MIND",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String reasonType,
            @Schema(description = "환불 상세 사유", example = "단순 변심입니다") String reasonDetail) {}
}
