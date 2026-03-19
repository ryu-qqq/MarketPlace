package com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/** 판매자 취소 일괄 요청. V3 스펙: orderId = 내부 orderItemId. */
@Schema(description = "판매자 취소 일괄 요청")
public record SellerCancelBatchApiRequest(
        @Schema(description = "취소 대상 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                @Valid
                List<SellerCancelItemApiRequest> items) {

    @Schema(description = "판매자 취소 개별 항목")
    public record SellerCancelItemApiRequest(
            @Schema(
                            description = "주문 상품 ID (UUIDv7)",
                            example = "01940001-0000-7000-8000-000000000001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String orderId,
            @Schema(
                            description = "취소 수량",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @Positive
                    int cancelQty,
            @Schema(
                            description = "취소 사유 유형",
                            example = "OUT_OF_STOCK",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String reasonType,
            @Schema(description = "취소 상세 사유", example = "재고 소진") String reasonDetail) {}
}
