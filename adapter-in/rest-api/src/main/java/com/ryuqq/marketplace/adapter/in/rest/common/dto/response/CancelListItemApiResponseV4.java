package com.ryuqq.marketplace.adapter.in.rest.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 취소 리스트 응답 (V4 스펙).
 *
 * <p>취소 관리 페이지는 claimInfo가 아닌 cancelInfo를 기대한다.
 * 반품/교환의 ClaimListItemApiResponseV4와 공통 중첩 타입을 공유하되, cancelInfo 필드명을 사용.
 */
@Schema(description = "취소 리스트 항목 (V4)")
public record CancelListItemApiResponseV4(
        @Schema(description = "주문번호") String orderNumber,
        @Schema(description = "주문 상품 정보") ClaimListItemApiResponseV4.OrderProductV4 orderProduct,
        @Schema(description = "취소 정보") CancelInfoV4 cancelInfo,
        @Schema(description = "구매자 정보") ClaimListItemApiResponseV4.BuyerInfoV4 buyerInfo,
        @Schema(description = "결제 정보") ClaimListItemApiResponseV4.PaymentV4 payment,
        @Schema(description = "수령인 정보") ClaimListItemApiResponseV4.ReceiverInfoV4 receiverInfo,
        @Schema(description = "외부몰 주문 정보") ClaimListItemApiResponseV4.ExternalOrderInfoV4 externalOrderInfo) {

    @Schema(description = "취소 정보 (V4)")
    public record CancelInfoV4(
            @Schema(description = "취소 ID") String cancelId,
            @Schema(description = "취소 번호") String cancelNumber,
            @Schema(description = "취소 유형 (BUYER_CANCEL, SELLER_CANCEL)") String type,
            @Schema(description = "취소 상태") String status,
            @Schema(description = "취소 수량") int cancelQty,
            @Schema(description = "취소 사유") ClaimListItemApiResponseV4.ReasonV4 reason,
            @Schema(description = "환불 정보") ClaimListItemApiResponseV4.RefundInfoV4 refundInfo,
            @Schema(description = "요청 일시") String requestedAt,
            @Schema(description = "완료 일시") String completedAt) {}
}
