package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 네이버 커머스 상품주문 상세.
 *
 * <p>product-orders/query API 응답의 개별 항목. 주문·상품주문·배송 정보를 포함.
 *
 * @param order 주문 레벨 정보
 * @param productOrder 상품주문 레벨 정보
 * @param delivery 배송 정보
 * @param exchange 교환 클레임 정보 (claimType=EXCHANGE 시 최상위 exchange 객체)
 * @param returnInfo 반품 클레임 정보 (claimType=RETURN 시 최상위 return 객체)
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
        justification = "Record DTO – immutable by convention")
public record NaverProductOrderDetail(
        NaverProductOrderOrder order,
        ProductOrderInfo productOrder,
        NaverDeliveryInfo delivery,
        NaverExchangeClaimInfo exchange,
        @JsonProperty("return") NaverReturnClaimInfo returnInfo) {

    /**
     * 상품주문 레벨 정보.
     *
     * @param productOrderId 상품주문번호
     * @param productOrderStatus 상품주문 상태
     * @param claimStatus 클레임 상태
     * @param claimType 클레임 구분
     * @param claimId 클레임 번호
     * @param placeOrderDate 발주 확인일 (ISO 8601)
     * @param placeOrderStatus 발주 상태 (NOT_YET/OK/CANCEL)
     * @param decisionDate 구매 확정일 (ISO 8601)
     * @param productClass 상품 종류 (일반/추가 상품)
     * @param groupProductId 그룹 상품 번호
     * @param productId 상품번호
     * @param originalProductId 원상품 번호
     * @param productName 상품명
     * @param productOption 옵션명
     * @param optionCode 옵션 코드
     * @param optionPrice 옵션 금액
     * @param sellerProductCode 판매자 상품코드
     * @param optionManageCode 옵션 관리코드
     * @param itemNo 아이템 번호
     * @param mallId 가맹점 ID
     * @param quantity 수량
     * @param initialQuantity 최초 수량
     * @param remainQuantity 잔여 수량
     * @param unitPrice 개당 판매가
     * @param totalProductAmount 총 상품금액 (할인 전)
     * @param initialProductAmount 최초 주문 금액
     * @param remainProductAmount 잔여 주문 금액
     * @param productDiscountAmount 상품 할인금액
     * @param totalPaymentAmount 총 결제금액 (할인 후)
     * @param initialPaymentAmount 최초 결제 금액
     * @param remainPaymentAmount 잔여 결제 금액
     * @param sellerBurdenDiscountAmount 판매자 부담 할인액
     * @param deliveryFeeAmount 배송비 합계
     * @param deliveryDiscountAmount 배송비 할인액
     * @param deliveryPolicyType 배송비 정책
     * @param shippingFeeType 배송비 형태 (선불/착불/무료)
     * @param packageNumber 묶음배송 번호
     * @param expectedDeliveryMethod 배송 방법 코드
     * @param expectedDeliveryCompany 택배사 코드
     * @param deliveryAttributeType 배송 속성 타입
     * @param shippingStartDate 발송 시작일 (ISO 8601)
     * @param shippingDueDate 발송 기한 (ISO 8601)
     * @param commissionRatingType 수수료 과금 구분
     * @param paymentCommission 결제 수수료
     * @param saleCommission 판매 수수료
     * @param channelCommission 채널 수수료
     * @param expectedSettlementAmount 정산 예정 금액
     * @param inflowPath 유입 경로
     * @param taxType 과면세 여부
     * @param shippingAddress 배송지 정보
     * @param shippingMemo 배송 요청사항
     * @param freeGift 사은품
     * @param currentClaim 현재 진행 중 클레임
     * @param completedClaims 완료된 클레임 목록
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
            justification = "Record DTO – immutable by convention")
    public record ProductOrderInfo(
            String productOrderId,
            String productOrderStatus,
            String claimStatus,
            String claimType,
            String claimId,
            String placeOrderDate,
            String placeOrderStatus,
            String decisionDate,
            String productClass,
            Long groupProductId,
            String productId,
            String originalProductId,
            String productName,
            String productOption,
            String optionCode,
            Integer optionPrice,
            String sellerProductCode,
            String optionManageCode,
            String itemNo,
            String mallId,
            int quantity,
            Integer initialQuantity,
            Integer remainQuantity,
            int unitPrice,
            int totalProductAmount,
            Integer initialProductAmount,
            Integer remainProductAmount,
            int productDiscountAmount,
            int totalPaymentAmount,
            Integer initialPaymentAmount,
            Integer remainPaymentAmount,
            Integer sellerBurdenDiscountAmount,
            Integer deliveryFeeAmount,
            Integer deliveryDiscountAmount,
            String deliveryPolicyType,
            String shippingFeeType,
            String packageNumber,
            String expectedDeliveryMethod,
            String expectedDeliveryCompany,
            String deliveryAttributeType,
            String shippingStartDate,
            String shippingDueDate,
            String commissionRatingType,
            Integer paymentCommission,
            Integer saleCommission,
            Integer channelCommission,
            Integer expectedSettlementAmount,
            String inflowPath,
            String taxType,
            NaverShippingAddress shippingAddress,
            String shippingMemo,
            String freeGift,
            NaverClaimInfo currentClaim,
            List<NaverClaimInfo> completedClaims) {}
}
